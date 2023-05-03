package me.kht.animetracker.ui.component.animedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.kht.animetracker.model.AnimeItem

@Composable
fun AnimeDetailRating(rating:AnimeItem.Rating?){
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()) {
            Text(text = "rank: #${rating?.rank?:"?"}")
            Text(text = "score: ${rating?.score?:"?"}")
            Text(text = "total: ${rating?.total?:"?"}")
        }
    }
}