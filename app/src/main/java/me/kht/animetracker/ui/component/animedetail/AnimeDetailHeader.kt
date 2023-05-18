package me.kht.animetracker.ui.component.animedetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.model.AnimeItem

@Composable
fun AnimeDetailHeader(animeItem: AnimeItem?,showAdd:Boolean=false,viewModel: MainViewModel){

    var contained:Boolean by remember { mutableStateOf(!showAdd) }
    LaunchedEffect(key1 = animeItem?.id){
        if (animeItem!=null) {
            contained = viewModel.watchListContains(animeItem.id)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = animeItem?.images?.medium, contentDescription = "", modifier = Modifier.width(150.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = animeItem?.nameCN?:"", style = MaterialTheme.typography.titleLarge)
            Text(text = animeItem?.name?:"", style = MaterialTheme.typography.titleSmall)
            if (showAdd) {
                AnimatedContent(targetState = contained) { state->
                        TextButton(onClick = {
                            viewModel.addItemToWatchList(viewModel.watchListTitle,animeItem!!.id)
                            contained = true
                        }, enabled = !state) {
                            Text(
                                text = if (state) stringResource(R.string.added,viewModel.watchListTitle) else stringResource(id = R.string.add_to_list),
                                color = if (state) Color.Gray else Color.Black
                            )
                        }
                }
            }
        }
    }
}