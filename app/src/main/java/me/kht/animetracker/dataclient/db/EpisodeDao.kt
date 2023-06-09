package me.kht.animetracker.dataclient.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.kht.animetracker.model.Episode

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEpisodes(episodes: List<Episode>)

    @Query("SELECT * FROM Episode WHERE animeId = :id")
    suspend fun getEpisodesByAnimeId(id: Int): List<Episode>

    @Query("SELECT * FROM Episode WHERE animeId = :id AND ep = :ep")
    suspend fun getEpisodeByAnimeId(id: Int, ep: Float): Episode?

    @Query("SELECT * FROM Episode")
    suspend fun getAllEpisodesStatic(): List<Episode>

    @Update
    suspend fun updateEpisodes(episode: List<Episode>)

    @Delete
    suspend fun deleteEpisodes(episode: List<Episode>)
}