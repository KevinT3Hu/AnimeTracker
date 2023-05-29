package me.kht.animetracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.ui.component.animedetail.AnimeDetailDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchListRoute(
    viewModel: MainViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val allWatchList = viewModel.allWatchList.collectAsState(emptyList())

    var showAnimeDetailDialog by remember { mutableStateOf(false) }
    var clickedAnimeItemId: Int? by remember { mutableStateOf(null) }

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
                        clickedAnimeItemId = animeItem.id
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
        if (clickedAnimeItemId==null){
            showAnimeDetailDialog=false
        }else{
            AnimeDetailDialog(animeId = clickedAnimeItemId!!, dismissDialog = { showAnimeDetailDialog=false }, viewModel = viewModel)
        }
    }

    if (viewModel.showDeleteWatchListDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleShowDeleteWatchListDialog(false) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteWatchList(allWatchList.value.find { it.watchList.title != viewModel.watchListTitle }?.watchList?.title)
                    viewModel.toggleShowDeleteWatchListDialog(false)
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleShowDeleteWatchListDialog(false) }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = { Text(text = stringResource(R.string.delete_watch_list)) },
            text = { Text(text = stringResource(R.string.delete_watch_list_confirm)) }
        )
    }
}