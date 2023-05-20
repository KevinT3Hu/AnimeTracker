package me.kht.animetracker.dataclient.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.kht.animetracker.model.AnimeState
import me.kht.animetracker.model.Episode
import me.kht.animetracker.model.WatchListAnimeStateCrossRef
import me.kht.animetracker.model.WatchListEntity

@Database(
    entities = [WatchListEntity::class, WatchListAnimeStateCrossRef::class, AnimeState::class, Episode::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class WatchListDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDao
    abstract fun animeStateDao(): AnimeStateDao
    abstract fun watchListCrossRefDao(): WatchListCrossRefDao
    abstract fun episodeDao(): EpisodeDao
}