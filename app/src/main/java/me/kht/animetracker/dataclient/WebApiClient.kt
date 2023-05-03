package me.kht.animetracker.dataclient

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import me.kht.animetracker.JsonSerializer
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.model.AnimeItem
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private const val BASE_URL = "https://api.bgm.tv"
private const val USER_AGENT = "kht/anime_tracker/1.0"

class WebApiClient {

    private val httpClient = OkHttpClient.Builder().addInterceptor(RequestPreProcessor()).build()

    suspend fun getAnimeItemById(id: Int): AnimeItem = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/v0/subjects/$id")
            .get()
            .build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val json = response.body?.string() ?: throw Exception("Empty response body")
            return@withContext JsonSerializer.decodeFromString<AnimeItem>(json)
        }
        throw WebRequestException(response.code)
    }

    suspend fun getAnimeEpisodes(id: Int) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/v0/episodes?subject_id=$id")
            .get()
            .build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val json = response.body?.string() ?: throw Exception("Empty response body")
            val jsonObject = JsonSerializer.decodeFromString<JsonObject>(json)
            val data = jsonObject["data"]
            return@withContext JsonSerializer.decodeFromString<List<Episode>>(data.toString())
        }
        throw WebRequestException(response.code)
    }

    class WebRequestException(val code: Int) : Exception()

    private class RequestPreProcessor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val newRequest = request.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", USER_AGENT)
                .build()
            Log.i("RequestPreProcessor", "Request: $request")
            return chain.proceed(newRequest)
        }
    }
}