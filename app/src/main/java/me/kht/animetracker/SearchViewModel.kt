package me.kht.animetracker

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.kht.animetracker.dataclient.WebApiClient
import me.kht.animetracker.model.AnimeSearchedItem

class SearchViewModel : ViewModel() {

    private val repository = AnimeDataRepository.getInstance()

    val searchResult = mutableStateListOf<AnimeSearchedItem>()

    fun searchByKeyword(
        keyword: String,
        state: LazyListState,
        scope: CoroutineScope,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = repository.searchAnimeItemByKeyword(keyword)
            searchResult.clear()
            searchResult.addAll(result)
        } catch (e: WebApiClient.WebRequestException) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        scope.launch {
            state.animateScrollToItem(0)
        }
    }
}