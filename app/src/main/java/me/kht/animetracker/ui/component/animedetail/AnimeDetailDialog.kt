package me.kht.animetracker.ui.component.animedetail

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.model.AnimeItem
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailDialog(animeId: Int, dismissDialog: () -> Unit,viewModel:MainViewModel,showAdd:Boolean=false) {

    val scope = rememberCoroutineScope()

    AlertDialog(onDismissRequest = { dismissDialog.invoke() }) {

        var animeItemFromWeb: AnimeItem? by remember { mutableStateOf(null) }
        LaunchedEffect(key1 = animeId){
            scope.launch {
                animeItemFromWeb = viewModel.getAnimeItemById(animeId)
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
                    animeItem = animeItemFromWeb,
                    showAdd = showAdd,
                    viewModel = viewModel,
                )
                Spacer(modifier = Modifier.height(10.dp))
                AnimeDetailRating(rating = animeItemFromWeb?.rating)
                AnimeDetailTags(tags = animeItemFromWeb?.tags?: emptyList())
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = animeItemFromWeb?.summary?:"")
            }
        }
    }
}