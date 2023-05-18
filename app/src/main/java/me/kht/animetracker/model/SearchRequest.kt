package me.kht.animetracker.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val keyword: String,
    val filter: Filter = Filter(),
    val sort:String = "match"
) {
    @Serializable
    data class Filter(
        val type: List<Int> = listOf(2),
        val nsfw: Boolean = false
    )
}
