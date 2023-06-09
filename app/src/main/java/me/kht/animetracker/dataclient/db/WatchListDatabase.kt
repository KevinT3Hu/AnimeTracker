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
    version = 4,
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

val migration2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // add new column "visibility" to AnimeState table, default value is true
        database.execSQL("ALTER TABLE AnimeState ADD COLUMN visibility INTEGER NOT NULL DEFAULT 1")
    }
}

val migration3_4 = object :Migration(3,4){
    override fun migrate(database: SupportSQLiteDatabase) {
        // alter column ep from integer to float in table Episode
        database.execSQL("CREATE TABLE Episode_new (id INTEGER PRIMARY KEY NOT NULL, animeId INTEGER NOT NULL, ep REAL NOT NULL, airDate TEXT NOT NULL)")
        database.execSQL("INSERT INTO Episode_new (id, animeId, ep, airDate) SELECT id, animeId, ep, airDate FROM Episode")
        database.execSQL("DROP TABLE Episode")
        database.execSQL("ALTER TABLE Episode_new RENAME TO Episode")

    }
}