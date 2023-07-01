package me.kht.animetracker.dataclient.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import me.kht.animetracker.model.WatchList
import me.kht.animetracker.model.WatchListAnimeStateCrossRef
import me.kht.animetracker.model.WatchListEntity

@Dao
interface WatchListCrossRefDao {

    @Transaction
    @Query("SELECT * FROM WatchList")
    fun getAllWatchLists(): Flow<List<WatchList>>

    @Transaction
    @Query("SELECT * FROM WatchList WHERE title = :title")
    fun getWatchListByTitle(title: String): Flow<WatchList>

    @Transaction
    @Query("SELECT * FROM WatchList WHERE title = :title")
    suspend fun getWatchListByTitleStatic(title: String): WatchList?

    @Transaction
    @Query("INSERT INTO WatchListAnimeStateCrossRef (title,animeId) VALUES (:title,:animeId)")
    suspend fun addAnimeStateToWatchList(animeId: Int, title: String)

    @Transaction
    @Query("SELECT * FROM WatchListAnimeStateCrossRef WHERE title = :title AND animeId = :animeId")
    suspend fun getAnimeStateFromWatchList(animeId: Int, title: String): WatchListAnimeStateCrossRef?

    @Transaction
    @Query("DELETE FROM WatchListAnimeStateCrossRef WHERE title = :title AND animeId = :animeId")
    suspend fun removeAnimeStateFromWatchList(animeId: Int, title: String)

    @Transaction
    @Query("DELETE FROM WatchListAnimeStateCrossRef WHERE title = :title")
    suspend fun deleteWatchList(title: String)

    @Transaction
    @Query("Select * FROM WatchListAnimeStateCrossRef")
    suspend fun getAllWatchListCrossRefStatic(): List<WatchListAnimeStateCrossRef>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchListCrossRefs(watchListCrossRefs: List<WatchListAnimeStateCrossRef>)
}

@Dao
interface WatchListDao {

    @Query("INSERT INTO WatchList (title,archived) VALUES (:title,0)")
    suspend fun createNewWatchList(title: String)

    @Query("DELETE FROM WatchList WHERE title = :title")
    suspend fun deleteWatchList(title: String)

    @Query("UPDATE WatchList SET archived = :archived WHERE title = :title")
    suspend fun archiveWatchList(title: String,archived: Boolean)

    @Query("SELECT * FROM WatchList")
    suspend fun getAllWatchListsStatic(): List<WatchListEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchLists(watchLists:List<WatchListEntity>)
}