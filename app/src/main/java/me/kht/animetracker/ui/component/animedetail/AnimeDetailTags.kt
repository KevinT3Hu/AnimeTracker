package me.kht.animetracker.ui.component.animedetail

import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.kht.animetracker.model.AnimeItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailTags(tags:List<AnimeItem.Tag>){
    Log.i("AnimeDetailTags", "tags: $tags")
    FlowRow() {
        tags.forEach { tag->
            val tagString = buildAnnotatedString {
                append(tag.name)
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(" ${tag.count}")
                }
            }
            SuggestionChip(
                onClick = {  },
                label = {
                    Text(text = tagString, style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAnimeDetailTags(){
    val tagsList = IntRange(1, 10).map { AnimeItem.Tag("Tag $it", it) }
    AnimeDetailTags(tagsList)
}