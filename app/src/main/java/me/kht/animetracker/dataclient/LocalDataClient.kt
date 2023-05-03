package me.kht.animetracker.dataclient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.dataclient.db.WatchListDatabase
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.model.AnimeState

class LocalDataClient(db: WatchListDatabase) {

    private val watchListDao = db.watchListDao()
    private val animeStateDao = db.animeStateDao()
    private val watchListCrossRefDao = db.watchListCrossRefDao()
    private val episodeDao = db.episodeDao()

    fun getAnimeStateById(id: Int) = animeStateDao.getAnimeStateByIdStatic(id)

    fun getAllWatchList() = watchListCrossRefDao.getAllWatchLists()

    fun createNewWatchList(title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListDao.createNewWatchList(title)
        }
    }

    fun addNewItemToWatchList(title: String, animeItem: AnimeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (animeStateDao.getAnimeStateByIdStatic(animeItem.id) == null) {
                val animeState = AnimeState(animeItem.id, animeItem)
                animeStateDao.insertAnimeState(animeState)
            }
            watchListCrossRefDao.addAnimeStateToWatchList(animeItem.id, title)
        }
    }

    fun addEpisodes(episodes: List<Episode>) {
        CoroutineScope(Dispatchers.IO).launch {
            episodeDao.insertEpisodes(episodes)
        }
    }

    fun getAllAnimeAssociatedWithWatchList(): Flow<Set<AnimeState>> =
        watchListCrossRefDao.getAllWatchLists().map {
            val animeStates = mutableSetOf<AnimeState>()
            for (watchList in it) {
                watchList.items.forEach { animeState ->
                    animeStates.add(animeState)
                }
            }
            animeStates
        }

    suspend fun getEpisodesByAnimeId(animeId: Int) = episodeDao.getEpisodesByAnimeId(animeId)

    suspend fun getEpisodeByAnimeId(animeId: Int, ep: Int) =
        episodeDao.getEpisodeByAnimeId(animeId, ep)

    fun removeItemFromWatchList(title: String, animeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListCrossRefDao.removeAnimeStateFromWatchList(animeId, title)
        }
    }

    fun markEpisodeWatchedState(animeId: Int, episodeIndex: Int, watched: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val animeState = animeStateDao.getAnimeStateByIdStatic(animeId)
            if (animeState != null) {
                animeState.markEpisodeWatchedState(episodeIndex, watched)
                animeStateDao.updateAnimeState(animeState)
            }
        }
    }

    fun deleteWatchList(title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListCrossRefDao.deleteWatchList(title)
            watchListDao.deleteWatchList(title)
        }
    }
}