package me.kht.animetracker.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R

@Composable
fun ArchivedRoute(viewModel: MainViewModel, navController: NavController) {
    val archivedWatchLists = viewModel.allWatchList.collectAsState(initial = emptyList())

    LazyColumn{
        items(archivedWatchLists.value.filter { it.watchList.archived }){
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                viewModel.watchListTitle = it.watchList.title
                navController.navigate("watchlist")
            }) {
                Row(modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                    Text(text = it.watchList.title, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = {
                        viewModel.unarchiveWatchList(it.watchList.title)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.unarchive), contentDescription = "")
                    }
                }
                Divider()
            }
        }
    }
}