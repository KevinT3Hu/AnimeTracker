package me.kht.animetracker.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

@OptIn(ExperimentalTextApi::class)
@Composable
fun EpisodeMarker(
    episode: Float,
    airState: EpisodeState,
    watched: Boolean,
    canvasSize: Dp,
    paddingValues: PaddingValues,
    onCheckedChange: (Boolean) -> Unit = {}
) {

    val fontSize = 16.sp
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = Color.Gray
    val enabled = airState != EpisodeState.NOT_AIRED

    val backgroundColor = if (watched) primaryContainerColor else Color.Transparent

    // if episode is integer, show it as integer
    val episodeText = if (episode % 1 == 0f) episode.toInt().toString() else episode.toString()
    val textColor = if (enabled) onSurfaceColor else secondaryColor

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(episodeText, TextStyle(fontSize = fontSize))
    val textSize = textLayoutResult.size

    val modifier =
        if (enabled) Modifier.clickable { onCheckedChange(!watched) } else Modifier.pointerInput(
            Unit
        ) {
            detectTapGestures { }
        }

    Canvas(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .padding(paddingValues)
            .requiredSize(canvasSize)
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        val center =
            Offset((canvasWidth - textSize.width) / 2f, (canvasHeight - textSize.height) / 2f)

        drawRoundRect(color = backgroundColor, cornerRadius = CornerRadius(5f, 5f))
        drawText(
            textMeasurer = textMeasurer,
            text = episodeText,
            style = TextStyle(color = textColor, fontSize = fontSize),
            topLeft = center
        )
        if (enabled) {
            // draw a triangle at the bottom right corner
            val triangleSize = canvasWidth * 0.15f
            val triangleOffset = Offset(canvasWidth - triangleSize, canvasHeight - triangleSize)
            drawPath(
                path = Path().apply {
                    moveTo(triangleOffset.x, triangleOffset.y)
                    lineTo(triangleOffset.x, triangleOffset.y - triangleSize)
                    lineTo(triangleOffset.x - triangleSize, triangleOffset.y)
                    close()

                },
                color = textColor,
                style = Fill
            )
        }

        if (airState == EpisodeState.TODAY) {
            // draw a play icon at the top right corner
            val playIconSize = canvasWidth * 0.2f
            val padding = canvasHeight * 0.1f
            val playIconOffset =
                Offset(canvasWidth - sqrt(3f) / 2 * playIconSize - padding, padding)
            drawPath(
                path = Path().apply {
                    moveTo(playIconOffset.x, playIconOffset.y)
                    lineTo(playIconOffset.x, playIconOffset.y + playIconSize)
                    lineTo(
                        playIconOffset.x + sqrt(3f) / 2 * playIconSize,
                        playIconOffset.y + playIconSize / 2f
                    )
                    close()
                },
                color = textColor,
                style = Fill
            )
        }

        if (enabled && !watched) {
            // draw an rounded corner outline
            drawRoundRect(
                color = primaryContainerColor,
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

enum class EpisodeState {
    AIRED,
    TODAY,
    NOT_AIRED
}