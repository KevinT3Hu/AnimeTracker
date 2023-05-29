package me.kht.animetracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.ui.component.animedetail.AnimeDetailDialog
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WatchListRoute(
    viewModel: MainViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val allWatchList = viewModel.allWatchList.collectAsState(emptyList())

    var showAnimeDetailDialog by remember { mutableStateOf(false) }
    var clickedAnimeItemId: Int? by remember { mutableStateOf(null) }

    var showInvisibleItemsDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val watchList =
        allWatchList.value.find { it.watchList.title == viewModel.watchListTitle }
    val visibleInWatchList = watchList?.items?.filter { it.visibility } ?: emptyList()
    val invisibleInWatchList = watchList?.items?.filter { !it.visibility } ?: emptyList()

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
        items(
            items = visibleInWatchList,
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
                horizontalPadding = 16.dp,
                selected = viewModel.selectedAnimeStates.contains(animeState)
            )
        }
        item {
            // add a spacer in case the bottom content is not visible
            Spacer(modifier = Modifier.padding(40.dp))
        }

        if (invisibleInWatchList.isNotEmpty()){
            // button to show invisible items
            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = {
                        showInvisibleItemsDialog = true
                    }, modifier = Modifier.padding(vertical = 20.dp)) {
                        Text(text = stringResource(id = R.string.show_invisible_items))
                    }
                }
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

    if (showInvisibleItemsDialog){

        LaunchedEffect(key1 = invisibleInWatchList.size){
            if (invisibleInWatchList.isEmpty()){
                showInvisibleItemsDialog=false
            }
        }

        AlertDialog(onDismissRequest = { showInvisibleItemsDialog=false }){
            Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(Dimension.alertDialogRoundedCorner)) {
                LazyColumn{
                    items(invisibleInWatchList){
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = it.animeItem.nameCN, modifier = Modifier.weight(9f))
                                IconButton(onClick = { viewModel.hideItem(it,false) }, modifier = Modifier.weight(1f)) {
                                    Icon(painter = painterResource(id = R.drawable.visibility_on), contentDescription = null)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
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