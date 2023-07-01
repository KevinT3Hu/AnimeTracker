package me.kht.animetracker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.kht.animetracker.dataclient.LocalDataClient
import me.kht.animetracker.dataclient.WebApiClient
import me.kht.animetracker.dataclient.db.WatchListDatabase
import me.kht.animetracker.dataclient.db.migration1_2
import me.kht.animetracker.dataclient.db.migration2_3
import me.kht.animetracker.dataclient.db.migration3_4
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.model.Episode

class AnimeDataRepository(db: WatchListDatabase) {

    private val webApiClient = WebApiClient()
    private val localDataClient = LocalDataClient(db)

    suspend fun getAnimeItemById(id: Int) = webApiClient.getAnimeItemById(id)

    suspend fun getEpisodesByAnimeIdFromApi(id: Int) = webApiClient.getAnimeEpisodes(id)

    fun getAllWatchList() = localDataClient.getAllWatchList()

    fun addNewItemToWatchList(title: String, animeItem: AnimeItem) =
        localDataClient.addNewItemToWatchList(title, animeItem)

    fun removeItemFromWatchList(title: String, animeId: Int) =
        localDataClient.removeItemFromWatchList(title, animeId)

    suspend fun getEpisodesByAnimeId(animeId: Int) = localDataClient.getEpisodesByAnimeId(animeId)

    suspend fun getEpisodeByAnimeId(animeId: Int, ep: Float) =
        localDataClient.getEpisodeByAnimeId(animeId, ep)

    fun addEpisodes(episodes: List<Episode>) = localDataClient.addEpisodes(episodes)

    fun getAllVisibleAnimeAssociatedWithWatchList() = localDataClient.getAllVisibleAnimeAssociatedWithWatchList()

    fun markEpisodeWatchedState(animeId: Int, episodeIndex: Float, watched: Boolean) =
        localDataClient.markEpisodeWatchedState(animeId, episodeIndex, watched)

    fun createNewWatchList(title: String) = localDataClient.createNewWatchList(title)

    suspend fun getLocalAnimeStateById(id: Int) = localDataClient.getAnimeStateById(id)

    fun deleteWatchList(title: String) = localDataClient.deleteWatchList(title)

    fun archiveWatchList(title: String,archived:Boolean=true) = localDataClient.archiveWatchList(title,archived)

    suspend fun searchAnimeItemByKeyword(keyword: String) =
        webApiClient.searchAnimeItemByKeyword(keyword)

    fun exportDatabase(context: Context, uri: Uri, onDone: (Boolean) -> Unit) =
        localDataClient.exportDatabase(context, uri, onDone)

    fun importDatabase(context: Context, uri: Uri, onDone: (Boolean) -> Unit) =
        localDataClient.importDatabase(context, uri, onDone)

    suspend fun watchListContains(watchListTitle: String, animeId: Int) =
        localDataClient.watchListContains(watchListTitle, animeId)

    fun refreshDatabase(watchListTitle: String?, workerThreadCount: Int = 10,progress: (Int, Int) -> Unit = { _, _ -> }) =
        CoroutineScope(Dispatchers.IO).launch {

            val animeStates = if (watchListTitle==null) localDataClient.getAllAnimeStatesStatic() else localDataClient.getWatchListByTitleStatic(watchListTitle)?.items?: emptyList()
            val episodes = localDataClient.getAllEpisodesStatic().filter {  episode ->
                animeStates.any { animeState -> animeState.animeId == episode.animeId }
            }

            val total = animeStates.size + episodes.size
            Log.i("AnimeDataRepository", "total: $total")
            Log.i("AnimeDataRepository", "animeStates: ${animeStates.size}")
            Log.i("AnimeDataRepository", "episodes: ${episodes.size}")
            var progressCount = 0

            if (animeStates.isNotEmpty()){
            val updatedAnimeStates = animeStates.map { state ->
                progress(++progressCount, total)
                state.copy(animeItem = webApiClient.getAnimeItemById(state.animeId))
            }
            localDataClient.updateAnimeStates(updatedAnimeStates)
        }

        if (episodes.isNotEmpty()){
            // divide episodes into chunks
            val episodeChunkSize = episodes.size/ workerThreadCount
            val episodeChunks = episodes.chunked(episodeChunkSize)
            episodeChunks.forEach { chunk ->
                val updatedEpisodes = chunk.map { episode ->
                    progress(++progressCount, total)
                    webApiClient.getEpisodeByEpisodeId(episode.id)
                }
                localDataClient.updateEpisodes(updatedEpisodes)
            }
        }
        }

    fun updateAnimeStateVisibility(animeId: Int, visibility: Boolean) =
        localDataClient.updateAnimeStateVisibility(animeId, visibility)

    companion object {
        private var instance: AnimeDataRepository? = null

        fun initInstance(context: Context) {
            val database = Room.databaseBuilder(
                context,
                WatchListDatabase::class.java,
                "watchlist"
            ).addMigrations(migration1_2, migration2_3, migration3_4).build()
            instance = AnimeDataRepository(database)
        }

        fun getInstance(): AnimeDataRepository {
            return instance ?: throw Exception("AnimeDataRepository not initialized")
        }
    }
}