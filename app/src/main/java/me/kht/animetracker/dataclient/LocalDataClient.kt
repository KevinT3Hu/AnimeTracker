package me.kht.animetracker.dataclient

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.kht.animetracker.JsonSerializer
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.dataclient.db.WatchListDatabase
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.model.AnimeState
import me.kht.animetracker.model.WatchListAnimeStateCrossRef
import me.kht.animetracker.model.WatchListEntity
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException

class LocalDataClient(db: WatchListDatabase) {

    private val watchListDao = db.watchListDao()
    private val animeStateDao = db.animeStateDao()
    private val watchListCrossRefDao = db.watchListCrossRefDao()
    private val episodeDao = db.episodeDao()

    suspend fun getAnimeStateById(id: Int) = animeStateDao.getAnimeStateByIdStatic(id)

    fun getAllWatchList() = watchListCrossRefDao.getAllWatchLists()

    fun createNewWatchList(title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListDao.createNewWatchList(title)
        }
    }

    fun addNewItemToWatchList(title: String, animeItem: AnimeItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (animeStateDao.getAnimeStateByIdStatic(animeItem.id) == null) {
                val animeState = AnimeState(animeItem.id, animeItem)
                animeStateDao.insertAnimeState(animeState)
            }
            watchListCrossRefDao.addAnimeStateToWatchList(animeItem.id, title)
        }
    }

    fun addEpisodes(episodes: List<Episode>) {
        CoroutineScope(Dispatchers.IO).launch {
            episodeDao.insertEpisodes(episodes)
        }
    }

    fun getAllAnimeAssociatedWithWatchList(): Flow<Set<AnimeState>> =
        watchListCrossRefDao.getAllWatchLists().map {
            val animeStates = mutableSetOf<AnimeState>()
            for (watchList in it) {
                watchList.items.forEach { animeState ->
                    animeStates.add(animeState)
                }
            }
            animeStates
        }

    suspend fun getEpisodesByAnimeId(animeId: Int) = episodeDao.getEpisodesByAnimeId(animeId)

    suspend fun getEpisodeByAnimeId(animeId: Int, ep: Int) =
        episodeDao.getEpisodeByAnimeId(animeId, ep)

    fun removeItemFromWatchList(title: String, animeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListCrossRefDao.removeAnimeStateFromWatchList(animeId, title)
        }
    }

    fun markEpisodeWatchedState(animeId: Int, episodeIndex: Int, watched: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val animeState = animeStateDao.getAnimeStateByIdStatic(animeId)
            if (animeState != null) {
                animeState.markEpisodeWatchedState(episodeIndex, watched)
                animeStateDao.updateAnimeState(animeState)
            }
        }
    }

    fun deleteWatchList(title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            watchListCrossRefDao.deleteWatchList(title)
            watchListDao.deleteWatchList(title)
        }
    }

    fun exportDatabase(context:Context,uri: Uri, onDone: (Boolean) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        val exportedWatchListString = JsonSerializer.encodeToString(watchListDao.getAllWatchListsStatic())
        val exportedAnimeStateString = JsonSerializer.encodeToString(animeStateDao.getAllAnimeStatesStatic())
        val exportedWatchListCrossRefString = JsonSerializer.encodeToString(watchListCrossRefDao.getAllWatchListCrossRefStatic())
        val exportedEpisodeString = JsonSerializer.encodeToString(episodeDao.getAllEpisodesStatic())

        val databaseJson = DatabaseJson(
            exportedWatchListString,
            exportedAnimeStateString,
            exportedWatchListCrossRefString,
            exportedEpisodeString
        )

        val databaseJsonString = JsonSerializer.encodeToString(databaseJson)

        try {
            withContext(Dispatchers.IO) {
                context.contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fos ->
                        fos.write(databaseJsonString.toByteArray())
                    }
                }
            }
        }catch (e:IOException){
            e.printStackTrace()
            onDone(false)
            return@launch
        }
        onDone(true)
    }

    fun importDatabase(context: Context,uri: Uri,onDone: (Boolean) -> Unit)=CoroutineScope(Dispatchers.IO).launch {
        val databaseJsonStringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri).use { inputStream ->
            if (inputStream==null){
                onDone(false)
                return@launch
            }
            BufferedReader(inputStream.reader()).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    databaseJsonStringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }

        val databaseJsonString = databaseJsonStringBuilder.toString()
        val databaseJson = JsonSerializer.decodeFromString<DatabaseJson>(databaseJsonString)

        val importedWatchList:List<WatchListEntity> = JsonSerializer.decodeFromString(databaseJson.watchListString)
        val importedAnimeState:List<AnimeState> = JsonSerializer.decodeFromString(databaseJson.animeStateString)
        val importedWatchListCrossRef:List<WatchListAnimeStateCrossRef> = JsonSerializer.decodeFromString(databaseJson.watchListCrossRefString)
        val importedEpisode:List<Episode> = JsonSerializer.decodeFromString(databaseJson.episodeString)

        watchListDao.insertWatchLists(importedWatchList)
        animeStateDao.insertAnimeStates(importedAnimeState)
        watchListCrossRefDao.insertWatchListCrossRefs(importedWatchListCrossRef)
        episodeDao.insertEpisodes(importedEpisode)

        onDone.invoke(true)
    }

    @Serializable
    private data class DatabaseJson(
        val watchListString: String,
        val animeStateString: String,
        val watchListCrossRefString: String,
        val episodeString: String
    )
}