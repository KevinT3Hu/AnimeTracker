package me.kht.animetracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchedItem(
    val id: Int,
    val image: String,
    val name: String,
    @SerialName("name_cn") val nameCN: String
)
