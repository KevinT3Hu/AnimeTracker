package me.kht.animetracker

import kotlinx.coroutines.test.runTest
import me.kht.animetracker.dataclient.WebApiClient
import org.junit.Test

class WebApiTest {

    @Test
    fun testSearch() = runTest {
        val webApiClient = WebApiClient()
        val keyword = "魔法少女毁灭者"
        try {
            val result = webApiClient.searchAnimeItemByKeyword(keyword)
            println(result)
            assert(result.isNotEmpty())
        } catch (e: WebApiClient.WebRequestException) {
            println("Error: ${e.code}, ${e.response.body.string()}")
            assert(false)
        }
    }
}