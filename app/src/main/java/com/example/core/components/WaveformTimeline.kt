package com.example.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NavyBorder

@Composable
fun WaveformStrip(
    samples: List<Float>,
    activeColor: Color,
    progressFraction: Float,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        if (samples.isEmpty()) return@Canvas
        val barSpacing = size.width / samples.size
        val barWidth = (barSpacing * 0.62f).coerceAtLeast(3f)

        samples.forEachIndexed { index, amplitude ->
            val x = index * barSpacing
            val barHeight = (size.height * amplitude).coerceAtLeast(6f)
            val y = (size.height - barHeight) / 2f
            val fraction = index.toFloat() / samples.size
            val barColor = when {
                isMuted -> NavyBorder
                fraction <= progressFraction -> activeColor
                else -> activeColor.copy(alpha = 0.35f)
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
