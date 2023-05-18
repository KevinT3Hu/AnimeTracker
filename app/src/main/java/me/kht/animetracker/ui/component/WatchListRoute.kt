package me.kht.animetracker.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.ui.component.animedetail.AnimeDetailHeader
import me.kht.animetracker.ui.component.animedetail.AnimeDetailRating
import me.kht.animetracker.ui.component.animedetail.AnimeDetailTags
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WatchListRoute(
    viewModel: MainViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val allWatchList = viewModel.allWatchList.collectAsState(emptyList())

    var showAnimeDetailDialog by remember { mutableStateOf(false) }
    var clickedAnimeItem: AnimeItem? by remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val watchList =
        allWatchList.value.find { it.watchList.title == viewModel.watchListTitle }

    DisposableEffect(lifecycleOwner) {

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    scrollState.scrollToItem(0)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LazyColumn(horizontalAlignment = Alignment.Start, state = scrollState) {
        if (watchList != null) {
            items(
                items = watchList.items,
                key = {
                    it.animeId
                }
            ) { animeState ->
                AnimeStateItem(
                    animeState = animeState,
                    viewModel = viewModel,
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { animeItem ->
                        clickedAnimeItem = animeItem
                        showAnimeDetailDialog = true
                    },
                    horizontalPadding = 16.dp
                )
            }
            item {
                // add a spacer in case the bottom content is not visible
                Spacer(modifier = Modifier.padding(40.dp))
            }
        }
    }

    if (showAnimeDetailDialog) {
        AlertDialog(onDismissRequest = { showAnimeDetailDialog=false }) {
            if (clickedAnimeItem == null) showAnimeDetailDialog = false

            var animeItemFromWeb:AnimeItem? by remember { mutableStateOf(null) }
            LaunchedEffect(key1 = clickedAnimeItem){
                scope.launch {
                    animeItemFromWeb = viewModel.getAnimeItemById(clickedAnimeItem!!.id)
                    Log.i("AnimeItem", animeItemFromWeb.toString())
                }
            }

            Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(Dimension.alertDialogRoundedCorner), modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 30.dp)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())) {
                    AnimeDetailHeader(
                        animeItem = clickedAnimeItem!!,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AnimeDetailRating(rating = animeItemFromWeb?.rating)
                    AnimeDetailTags(tags = animeItemFromWeb?.tags?: emptyList())
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = clickedAnimeItem?.summary ?: "No description")
                }
            }
        }
    }
}