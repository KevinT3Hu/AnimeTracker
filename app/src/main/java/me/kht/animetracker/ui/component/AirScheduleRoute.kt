package me.kht.animetracker.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun AirScheduleRoute(viewModel: MainViewModel) {

    val allAnime = viewModel.allAnimeAssociatedWithWatchList.collectAsState(emptyList())

    LaunchedEffect(key1 = allAnime.value) {
        Log.i("AirScheduleRoute", "allAnime.value: ${allAnime.value}")
        viewModel.updateAnimeMap()
    }

    val dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.date_format))

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        Log.i("AirScheduleRoute", "animeAirDateMap: ${viewModel.animeAirDateMap.entries.toList()}")
        viewModel.weekInTheFuture.forEach { date ->
            val animeItems = viewModel.animeAirDateMap[date]
            if (animeItems.isNullOrEmpty()) return@forEach

            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = date.format(dateTimeFormatter),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            item {
                FlowRow {
                    animeItems.forEach { animeItem ->
                        val imageWidth = 150.dp

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = animeItem.images.common,
                                contentDescription = "",
                                modifier = Modifier.width(imageWidth)
                            )
                            Text(
                                text = animeItem.nameCN,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .width(imageWidth)
                                    .basicMarquee(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

        }
    }
}