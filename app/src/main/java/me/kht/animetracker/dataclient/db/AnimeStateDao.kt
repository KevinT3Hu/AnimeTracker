package me.kht.animetracker.dataclient.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.kht.animetracker.model.AnimeState

@Dao
interface AnimeStateDao {

    @Insert(entity = AnimeState::class)
    suspend fun insertAnimeState(animeState: AnimeState)

    @Insert(entity = AnimeState::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnimeStates(animeStates: List<AnimeState>)

    @Update(entity = AnimeState::class)
    suspend fun updateAnimeState(animeState: AnimeState)

    @Query("SELECT * FROM AnimeState WHERE animeId = :id")
    fun getAnimeStateById(id: Int): Flow<AnimeState?>

    @Query("SELECT * FROM AnimeState WHERE animeId = :id")
    suspend fun getAnimeStateByIdStatic(id: Int): AnimeState?

    @Query("SELECT * FROM AnimeState")
    suspend fun getAllAnimeStatesStatic(): List<AnimeState>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateAnimeStates(animeStates: List<AnimeState>)

    @Query("UPDATE AnimeState SET visibility = :visibility WHERE animeId = :id")
    suspend fun updateAnimeStateVisibility(id: Int, visibility: Boolean)

    @Delete
    suspend fun deleteAnimeStates(animeStates:List<AnimeState>)
}