package me.kht.animetracker.dataclient.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import me.kht.animetracker.model.WatchList

@Dao
interface WatchListCrossRefDao {

    @Transaction
    @Query("SELECT * FROM WatchList")
    fun getAllWatchLists(): Flow<List<WatchList>>

    @Transaction
    @Query("SELECT * FROM WatchList WHERE title = :title")
    fun getWatchListByTitle(title: String): Flow<WatchList>

    @Transaction
    @Query("INSERT INTO WatchListAnimeStateCrossRef (title,animeId) VALUES (:title,:animeId)")
    suspend fun addAnimeStateToWatchList(animeId: Int, title: String)

    @Transaction
    @Query("DELETE FROM WatchListAnimeStateCrossRef WHERE title = :title AND animeId = :animeId")
    suspend fun removeAnimeStateFromWatchList(animeId: Int, title: String)

    @Transaction
    @Query("DELETE FROM WatchListAnimeStateCrossRef WHERE title = :title")
    suspend fun deleteWatchList(title: String)
}

@Dao
interface WatchListDao {

    @Query("INSERT INTO WatchList (title) VALUES (:title)")
    suspend fun createNewWatchList(title: String)

    @Query("DELETE FROM WatchList WHERE title = :title")
    suspend fun deleteWatchList(title: String)
}