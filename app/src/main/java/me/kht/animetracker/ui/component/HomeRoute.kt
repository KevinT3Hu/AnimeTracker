package me.kht.animetracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeRoute(viewModel: MainViewModel) {

    val allWatchList = viewModel.allWatchList.collectAsState(emptyList())

    LaunchedEffect(key1 = allWatchList.value) {
        viewModel.updateAnimeLists()
    }

    val contentModifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()

    LazyColumn {

        // On Air Today Title
        item {
            Column(modifier = contentModifier) {
                Text(
                    text = stringResource(R.string.on_air_today),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.animeAirToday.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_anime_on_air_today),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray
                    )
                }
            }
        }

        // On Air Today
        item {
            LazyRow(contentModifier) {

                val imageSize = 150.dp

                items(viewModel.animeAirToday) { animeItem ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .clickable { }) {
                        AsyncImage(
                            model = animeItem.images.small,
                            contentDescription = "",
                            modifier = Modifier.width(imageSize)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = animeItem.nameCN,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .width(imageSize)
                                .basicMarquee(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Not Watched Title
        item {
            Column(modifier = contentModifier) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.not_watched),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.episodeNotWatched.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_aired_episodes_unwatched),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray
                    )
                }
            }
        }

        // Not Watched
        items(viewModel.episodeNotWatched.keys.toList()) { animeItem ->
            val episodes = viewModel.episodeNotWatched[animeItem]
            if (episodes != null) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 5.dp, horizontal = 16.dp)) {
                    AsyncImage(model = animeItem.images.small, contentDescription = "")

                    Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = animeItem.nameCN,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(25.dp))
                            LazyRow {
                                items(episodes) { ep ->
                                    EpisodeMarker(
                                        episode = ep.ep,
                                        airState = viewModel.getEpisodeState(ep),
                                        watched = false,
                                        canvasSize = 40.dp,
                                        paddingValues = PaddingValues(5.dp, 5.dp),
                                        onCheckedChange = {
                                            viewModel.markEpisodeWatchedState(
                                                animeItem.id,
                                                ep.ep,
                                                it
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

}