package me.kht.animetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Episode(
    @PrimaryKey val id: Int,
    @SerialName("subject_id") val animeId: Int,
    val ep: Float,
    @SerialName("airdate") val airDate: String,
)
