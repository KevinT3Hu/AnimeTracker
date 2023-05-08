package me.kht.animetracker

import android.content.Context
import androidx.room.Room
import me.kht.animetracker.dataclient.LocalDataClient
import me.kht.animetracker.dataclient.WebApiClient
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.dataclient.db.WatchListDatabase
import me.kht.animetracker.model.AnimeItem

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

    suspend fun getEpisodeByAnimeId(animeId: Int, ep: Int) =
        localDataClient.getEpisodeByAnimeId(animeId, ep)

    fun addEpisodes(episodes: List<Episode>) = localDataClient.addEpisodes(episodes)

    fun getAllAnimeAssociatedWithWatchList() = localDataClient.getAllAnimeAssociatedWithWatchList()

    fun markEpisodeWatchedState(animeId: Int, episodeIndex: Int, watched: Boolean) =
        localDataClient.markEpisodeWatchedState(animeId, episodeIndex, watched)

    fun createNewWatchList(title: String) = localDataClient.createNewWatchList(title)

    fun getLocalAnimeStateById(id: Int) = localDataClient.getAnimeStateById(id)

    fun deleteWatchList(title: String) = localDataClient.deleteWatchList(title)

    suspend fun searchAnimeItemByKeyword(keyword: String) =
        webApiClient.searchAnimeItemByKeyword(keyword)

    companion object {
        private var instance: AnimeDataRepository? = null

        fun initInstance(context: Context) {
            val database = Room.databaseBuilder(
                context,
                WatchListDatabase::class.java,
                "watchlist"
            ).build()
            instance = AnimeDataRepository(database)
        }

        fun getInstance(): AnimeDataRepository {
            return instance ?: throw Exception("AnimeDataRepository not initialized")
        }
    }
}