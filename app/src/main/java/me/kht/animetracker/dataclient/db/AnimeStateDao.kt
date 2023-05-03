package me.kht.animetracker.dataclient.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.kht.animetracker.model.AnimeState

@Dao
interface AnimeStateDao {

    @Insert(entity = AnimeState::class)
    suspend fun insertAnimeState(animeState: AnimeState)

    @Update(entity = AnimeState::class)
    suspend fun updateAnimeState(animeState: AnimeState)


    @Query("SELECT * FROM AnimeState WHERE animeId = :id")
    fun getAnimeStateById(id: Int): Flow<AnimeState?>

    @Query("SELECT * FROM AnimeState WHERE animeId = :id")
    fun getAnimeStateByIdStatic(id: Int): AnimeState?
}