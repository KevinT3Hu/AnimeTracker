package me.kht.animetracker.dataclient.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.kht.animetracker.model.AnimeState
import me.kht.animetracker.model.Episode
import me.kht.animetracker.model.WatchListAnimeStateCrossRef
import me.kht.animetracker.model.WatchListEntity

@Database(
    entities = [WatchListEntity::class, WatchListAnimeStateCrossRef::class, AnimeState::class, Episode::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class WatchListDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDao
    abstract fun animeStateDao(): AnimeStateDao
    abstract fun watchListCrossRefDao(): WatchListCrossRefDao
    abstract fun episodeDao(): EpisodeDao
}

val migration1_2 = object :Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        // add new column "archived" to WatchList table, default value is false
        database.execSQL("ALTER TABLE WatchList ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
    }

}