package me.kht.animetracker.dataclient

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import me.kht.animetracker.JsonSerializer
import me.kht.animetracker.dataclient.db.Episode
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.model.AnimeSearchedItem
import me.kht.animetracker.model.SearchRequest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
            val json = response.body.string()
            return@withContext JsonSerializer.decodeFromString<AnimeItem>(json)
        }
        throw WebRequestException(response.code, response)
    }

    suspend fun searchAnimeItemByKeyword(keyword: String): List<AnimeSearchedItem> =
        withContext(Dispatchers.IO) {
            val requestBody = JsonSerializer.encodeToString(SearchRequest(keyword))
                .toRequestBody("application/json".toMediaTypeOrNull()!!)
            val request = Request.Builder()
                .url("$BASE_URL/v0/search/subjects")
                .post(requestBody)
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body.string()
                val jsonObject = JsonSerializer.decodeFromString<JsonObject>(json)
                val data = jsonObject["data"]
                return@withContext JsonSerializer.decodeFromString<List<AnimeSearchedItem>>(data.toString())
            }
            throw WebRequestException(response.code, response, "keyword=$keyword")
        }

    suspend fun getAnimeEpisodes(id: Int) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/v0/episodes?subject_id=$id")
            .get()
            .build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val json = response.body.string()
            val jsonObject = JsonSerializer.decodeFromString<JsonObject>(json)
            val data = jsonObject["data"]
            return@withContext JsonSerializer.decodeFromString<List<Episode>>(data.toString())
        }
        throw WebRequestException(response.code, response, "id=$id")
    }

    class WebRequestException(val code: Int, val response: Response, extra: Any? = null) :
        Exception("Request failed with code $code . Response: $response . Extra: $extra")

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