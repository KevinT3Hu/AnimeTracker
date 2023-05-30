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
    // episode index
    val watchedEpisodes: MutableSet<Float> = mutableSetOf(),
    val visibility: Boolean = true,
) {
    fun markEpisodeWatchedState(episodeIndex: Float, watched: Boolean) {
        if (watched) {
            watchedEpisodes.add(episodeIndex)
        } else {
            watchedEpisodes.remove(episodeIndex)
        }
    }
}
