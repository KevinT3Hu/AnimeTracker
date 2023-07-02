package me.kht.animetracker.model

import androidx.room.Embedded
import androidx.room.Ignore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeItem(
    val id: Int,
    val name: String,
    @SerialName("name_cn") val nameCN: String,
    val summary: String,
    val date: String,
    @Embedded(prefix = "image_") val images: ImageSet,
    val eps: Int,
    @SerialName("total_episodes") val totalEpisodes: Int,
    @Ignore val tags: List<Tag>?=null,
    @Ignore val rating: Rating?=null,
) {

    constructor(
        id: Int,
        name: String,
        nameCN: String,
        summary: String,
        date: String,
        images: ImageSet,
        eps: Int,
        totalEpisodes: Int,
    ) : this(id, name, nameCN, summary, date, images, eps, totalEpisodes, null, null)

    @Serializable
    data class ImageSet(
        val large: String,
        val common: String,
        val medium: String,
        val small: String,
    )

    @Serializable
    data class Tag(
        val name:String,
        val count:Int
    )

    @Serializable
    data class Rating(
        val rank:Int,
        val total:Int,
        val score:Float
    )
}
