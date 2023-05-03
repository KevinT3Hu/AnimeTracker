package me.kht.animetracker.ui.component.animedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.kht.animetracker.model.AnimeItem

@Composable
fun AnimeDetailHeader(animeItem: AnimeItem){
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = animeItem.images.medium, contentDescription = "", modifier = Modifier.width(150.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = animeItem.nameCN, style = MaterialTheme.typography.titleLarge)
            Text(text = animeItem.name, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Preview
@Composable
private fun PreviewAnimeDetail(){
    AnimeDetailHeader(animeItem = AnimeItem(
        id = 1,
        name = "name",
        nameCN = "nameCN",
        summary = "summary",
        date = "date",
        images = AnimeItem.ImageSet(
            large = "large",
            common = "https://lain.bgm.tv/r/400/pic/cover/l/da/43/368116_fM4z8.jpg",
            medium = "https://lain.bgm.tv/r/800/pic/cover/l/da/43/368116_fM4z8.jpg",
            small = "small",
        ),
        eps = 1,
        totalEpisodes = 1,
    ))
}