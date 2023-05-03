package me.kht.animetracker.dataclient.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Episode(
    @PrimaryKey val id: Int,
    @SerialName("subject_id") val animeId: Int,
    val ep: Int,
    @SerialName("airdate") val airDate: String,
)
