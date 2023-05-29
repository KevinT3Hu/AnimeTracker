package me.kht.animetracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.model.AnimeState

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun AnimeStateItem(
    animeState: AnimeState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onClick: (AnimeItem) -> Unit = {},
    horizontalPadding:Dp = 0.dp,
    selected:Boolean = false
) {

    val scope = rememberCoroutineScope()

    val episodes = List(animeState.animeItem.eps) { it + 1 }

    val canvasSize = 40.dp
    val canvasHorizontalPadding = 5.dp
    val padding = PaddingValues(horizontal = canvasHorizontalPadding, vertical = 5.dp)

    val backgroundColor = if (selected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface

    Box(modifier = modifier.wrapContentSize(align = Alignment.TopStart).background(backgroundColor)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (viewModel.actionMode){
                        viewModel.toggleItem(animeState)
                    }else{
                        onClick.invoke(animeState.animeItem)
                    }
                },
                onLongClick = { viewModel.selectItem(animeState) }
            )
            .padding(horizontal = horizontalPadding), horizontalAlignment = Alignment.Start
        ) {

            Row(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                AsyncImage(
                    model = animeState.animeItem.images.small,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Column {
                    Text(
                        text = animeState.animeItem.nameCN,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = animeState.animeItem.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    FlowRow {
                        episodes.forEach { ep->
                            var epSelected by remember {
                                mutableStateOf(
                                    animeState.watchedEpisodes.contains(
                                        ep
                                    )
                                )
                            }

                            var airState by remember { mutableStateOf(EpisodeState.NOT_AIRED) }
                            scope.launch {
                                airState = viewModel.isEpisodeAired(animeState.animeItem.id, ep)
                            }
                            EpisodeMarker(
                                ep,
                                airState,
                                epSelected,
                                canvasSize = canvasSize,
                                paddingValues = padding
                            ) {
                                viewModel.markEpisodeWatchedState(
                                    animeState.animeItem.id,
                                    ep,
                                    it
                                )
                                epSelected = it
                            }
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }
    }
}