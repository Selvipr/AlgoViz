package com.example.algoviz.ui.screens.visualize.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import com.example.algoviz.ui.theme.DeepNavy
import com.example.algoviz.ui.theme.InfoBlue
import com.example.algoviz.ui.theme.MintAccent
import com.example.algoviz.ui.theme.OrangeAccent
import kotlin.math.max

@Composable
fun ArrayCanvas(
    step: VisualizationStep?,
    modifier: Modifier = Modifier,
) {
    if (step == null || step.state !is VisualizationState.ArrayState) return
    
    val arrayState = step.state as VisualizationState.ArrayState
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight
        val arrSize = max(arrayState.array.size, 1)

        val padding = 16.dp
        val availableWidth = width - (padding * 2)
        val spacing = 8.dp
        
        // Calculate max value for height scaling
        val maxVal = max(arrayState.array.maxOfOrNull { it.value } ?: 1, 1).toFloat()
        val barWidth = (availableWidth - (spacing * (arrSize - 1))) / arrSize

        val maxBarHeight = height * 0.7f // Bars take up max 70% of canvas height

        // Draw static indices
        for (i in 0 until arrSize) {
            val targetX = padding + (barWidth + spacing) * i
            
            // To properly center the index text under the bar, we need its approximate width, or use a Box
            Box(
                modifier = Modifier
                    .offset(x = targetX, y = height - padding + 4.dp)
                    .size(width = barWidth, height = 20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = i.toString(),
                    color = onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        // Draw animated array bars
        arrayState.array.forEachIndexed { i, node ->
            key(node.id) {
                val value = node.value
                val targetHeight = (value / maxVal) * maxBarHeight.value
                val targetX = padding + (barWidth + spacing) * i
                
                val barColor = when {
                    i in arrayState.sortedIndices -> MintAccent
                    arrayState.swapped && i in arrayState.comparingIndices -> OrangeAccent
                    i in arrayState.comparingIndices -> InfoBlue
                    else -> surfaceVariant
                }

                val textColor = if (barColor == surfaceVariant) onSurface else DeepNavy

                AnimatedArrayBar(
                    value = value,
                    targetHeight = targetHeight.dp,
                    targetX = targetX,
                    barWidth = barWidth,
                    barColor = barColor,
                    textColor = textColor,
                    canvasHeight = height,
                    padding = padding
                )
            }
        }
    }
}

@Composable
fun AnimatedArrayBar(
    value: Int,
    targetHeight: Dp,
    targetX: Dp,
    barWidth: Dp,
    barColor: Color,
    textColor: Color,
    canvasHeight: Dp,
    padding: Dp
) {
    val xOffset by animateDpAsState(targetValue = targetX, animationSpec = tween(400), label = "xOffset")
    val color by animateColorAsState(targetValue = barColor, animationSpec = tween(300), label = "color")
    val animTextColor by animateColorAsState(targetValue = textColor, animationSpec = tween(300), label = "textColor")
    
    // Y position for the bottom of the bar
    val yOffset = canvasHeight - padding - targetHeight
    
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset - 24.dp) // 24.dp space above for text
            .size(width = barWidth, height = targetHeight + 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Value Text
        Text(
            text = value.toString(),
            color = animTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // The colored bar
        Box(
            modifier = Modifier
                .size(width = barWidth, height = targetHeight)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
    }
}
