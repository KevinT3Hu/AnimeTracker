package me.kht.animetracker.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class AnimeState(
    @PrimaryKey @ColumnInfo(index = true) val animeId: Int,
    @Embedded val animeItem: AnimeItem,
    val favorite: Boolean = false,
    val watchedEpisodes: MutableSet<Int> = mutableSetOf()
) {
    fun markEpisodeWatchedState(episodeIndex: Int, watched: Boolean) {
        if (watched) {
            watchedEpisodes.add(episodeIndex)
        } else {
            watchedEpisodes.remove(episodeIndex)
        }
    }
}
