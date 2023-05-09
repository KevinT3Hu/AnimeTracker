package me.kht.animetracker

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.ui.component.EpisodeState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

class MainViewModel : ViewModel() {

    private val repository = AnimeDataRepository.getInstance()

    private val episodeStateCache = mutableMapOf<String, EpisodeState>()

    val allWatchList = repository.getAllWatchList()
    val allAnimeAssociatedWithWatchList = repository.getAllAnimeAssociatedWithWatchList()
    private val _watchListTitle = mutableStateOf("")
    var watchListTitle: String
        get() = _watchListTitle.value
        set(value) {
            _watchListTitle.value = value
        }

    val animeAirToday = mutableStateListOf<AnimeItem>()
    val episodeNotWatched = mutableStateMapOf<AnimeItem, List<Episode>>()

    val animeAirDateMap = mutableStateMapOf<ZonedDateTime, List<AnimeItem>>()

    val weekInTheFuture = IntRange(0, 6).map {
        val zonedDateTime = ZonedDateTime.now().plusDays(it.toLong())
        // set time to 00:00:00
        val time = ZonedDateTime.of(
            zonedDateTime.year,
            zonedDateTime.monthValue,
            zonedDateTime.dayOfMonth,
            0,
            0,
            0,
            0,
            ZoneId.systemDefault()
        )
        time
    }

    suspend fun isEpisodeAired(animeId: Int, episodeIndex: Int): EpisodeState {
        val cacheKey = "$animeId-$episodeIndex"
        if (episodeStateCache.containsKey(cacheKey)) {
            Log.i("MainViewModel", "Cache hit for $cacheKey")
            return episodeStateCache[cacheKey]!!
        }
        return withContext(Dispatchers.IO) {
            var episode = repository.getEpisodeByAnimeId(animeId, episodeIndex)
            if (episode == null) {
                val episodes = repository.getEpisodesByAnimeIdFromApi(animeId)
                episode = episodes.find { it.ep == episodeIndex }!!
                repository.addEpisodes(episodes)
            }
            val episodeState = getEpisodeState(episode)
            episodeStateCache["$animeId-$episodeIndex"] = episodeState
            episodeState
        }
    }

    suspend fun getAnimeItemById(animeId: Int): AnimeItem {
        return repository.getAnimeItemById(animeId)
    }

    fun addItemToWatchList(title: String, animeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var item = repository.getLocalAnimeStateById(animeId)?.animeItem
                ?: repository.getAnimeItemById(animeId)
            // sometimes the eps returned in this data is 0
            // in this case, use the get episodes to retrieve the eps
            if (item.eps==0){
                val eps = repository.getEpisodesByAnimeIdFromApi(animeId)
                item = item.copy(eps = eps.size)
            }
            repository.addNewItemToWatchList(title, item)

            if (repository.getEpisodesByAnimeId(animeId).isEmpty()) {

                repository.addEpisodes(repository.getEpisodesByAnimeIdFromApi(animeId))
            }
        }
    }

    fun removeItemFromWatchList(animeId: Int, title: String = watchListTitle) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.removeItemFromWatchList(title, animeId)
        }
    }

    fun newWatchList(title: String) {
        repository.createNewWatchList(title)
        watchListTitle = title
    }

    fun markEpisodeWatchedState(animeId: Int, episodeIndex: Int, watched: Boolean) {
        repository.markEpisodeWatchedState(animeId, episodeIndex, watched)
        // initAnimeLists()
    }

    fun deleteWatchList(nextWatchList: String?) {
        repository.deleteWatchList(watchListTitle)
        if (nextWatchList != null) {
            watchListTitle = nextWatchList
        } else {
            newWatchList("Default")
        }
    }

    fun updateAnimeLists() {
        CoroutineScope(Dispatchers.IO).launch {
            allAnimeAssociatedWithWatchList.collectLatest { animeSet ->
                animeSet.forEach { animeState ->
                    val episodes = repository.getEpisodesByAnimeId(animeState.animeItem.id)
                    val episodesAiredToday = episodes.filter { episode ->
                        episode.ep != 0 &&
                                getEpisodeState(episode) == EpisodeState.TODAY
                    }
                    if (episodesAiredToday.isNotEmpty() && !animeAirToday.contains(animeState.animeItem)) {
                        animeAirToday.add(animeState.animeItem)
                    }
                    val episodesNotWatched = episodes.filter { episode ->
                        episode.ep != 0 &&
                                getEpisodeState(episode) != EpisodeState.NOT_AIRED &&
                                animeState.watchedEpisodes.find { epi -> epi == episode.ep } == null
                    }
                    if (episodesNotWatched.isNotEmpty()) {
                        episodeNotWatched[animeState.animeItem] = episodesNotWatched
                    } else {
                        episodeNotWatched.remove(animeState.animeItem)
                    }
                }
            }
        }
    }

    fun getEpisodeState(episode: Episode): EpisodeState {
        val airDate = episode.airDate.split("-")
        if (airDate.size != 3) return EpisodeState.NOT_AIRED
        val year = airDate[0].toInt()
        val month = airDate[1].toInt()
        val day = airDate[2].toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val today = Calendar.getInstance()
        val episodeState = if (calendar.before(today)) {
            EpisodeState.AIRED
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        ) {
            EpisodeState.TODAY
        } else {
            EpisodeState.NOT_AIRED
        }
        return episodeState
    }

    fun updateAnimeMap() = CoroutineScope(Dispatchers.IO).launch {

        weekInTheFuture.forEach {
            animeAirDateMap[it] = emptyList()
        }
        Log.i("MainViewModel", "Updating anime map for $weekInTheFuture")
        allAnimeAssociatedWithWatchList.collectLatest { animeSet ->
            animeSet.forEach {
                val episodes = repository.getEpisodesByAnimeId(it.animeId)
                var nextEpisodeDate: ZonedDateTime? = null
                episodes.forEach inner@{ episode ->

                    val airDate = episode.airDate.split("-")
                    if (airDate.size != 3) return@inner
                    val year = airDate[0].toInt()
                    val month = airDate[1].toInt()
                    val day = airDate[2].toInt()
                    val time =
                        ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault())

                    if (episode.ep != 0 &&
                        weekInTheFuture.contains(time)
                    ) {
                        nextEpisodeDate = time
                    }
                }

                if (nextEpisodeDate != null) {
                    val list = animeAirDateMap[nextEpisodeDate!!]!!
                    if (!list.contains(it.animeItem)) {
                        animeAirDateMap[nextEpisodeDate!!] = list + it.animeItem
                    }
                }
            }
        }
    }
}