package me.kht.animetracker.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "WatchList")
data class WatchListEntity(
    @PrimaryKey @ColumnInfo(index = true) val title: String,
)

@Serializable
@Entity(primaryKeys = ["title", "animeId"])
data class WatchListAnimeStateCrossRef(
    val title: String,
    @ColumnInfo(index = true) val animeId: Int
)

data class WatchList(
    @Embedded val watchList: WatchListEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "animeId",
        associateBy = Junction(WatchListAnimeStateCrossRef::class)
    )
    val items: List<AnimeState>
)
