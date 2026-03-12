package com.example.algoviz.ui.screens.visualize.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algoviz.domain.engine.ArrayStep
import com.example.algoviz.ui.theme.InfoBlue
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent
import kotlin.math.max

@Composable
fun ArrayCanvas(
    step: ArrayStep?,
    modifier: Modifier = Modifier,
) {
    if (step == null) return

    val textMeasurer = rememberTextMeasurer()
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val arrSize = max(step.array.size, 1)

        val padding = 16.dp.toPx()
        val availableWidth = width - (padding * 2)
        val spacing = 8.dp.toPx()
        
        // Calculate max value for height scaling
        val maxVal = max(step.array.maxOrNull() ?: 1, 1).toFloat()
        val barWidth = (availableWidth - (spacing * (arrSize - 1))) / arrSize

        val maxBarHeight = height * 0.7f // Bars take up max 70% of canvas height

        for (i in 0 until arrSize) {
            val value = step.array[i]
            val barHeight = (value / maxVal) * maxBarHeight

            val xOffset = padding + (i * (barWidth + spacing))
            val yOffset = height - barHeight - padding

            // Determine bar color
            val barColor = when {
                i in step.sortedIndices -> MintAccent
                step.swapped && i in step.comparingIndices -> OrangeAccent
                i in step.comparingIndices -> InfoBlue
                else -> surfaceVariant
            }

            // Draw the bar
            drawRoundRect(
                color = barColor,
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Draw the value text above the bar
            val textLayoutResult = textMeasurer.measure(
                text = value.toString(),
                style = TextStyle(
                    color = onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = xOffset + (barWidth - textLayoutResult.size.width) / 2,
                    y = yOffset - textLayoutResult.size.height - 4.dp.toPx()
                )
            )
            
            // Draw index below the bar
            val indexTextResult = textMeasurer.measure(
                text = i.toString(),
                style = TextStyle(
                    color = onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            )
            
            drawText(
                textLayoutResult = indexTextResult,
                topLeft = Offset(
                    x = xOffset + (barWidth - indexTextResult.size.width) / 2,
                    y = height - padding + 4.dp.toPx()
                )
            )
        }
    }
}
