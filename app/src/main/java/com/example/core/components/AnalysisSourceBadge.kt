package com.example.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.AnalysisSource
import com.example.ui.theme.RealEmerald
import com.example.ui.theme.RealEmeraldContainer
import com.example.ui.theme.SampleAmber
import com.example.ui.theme.SampleAmberContainer

/**
 * Explicitly distinguishes initial sample previews from real Demucs AI cloud analyses.
 */
@Composable
fun AnalysisSourceBadge(
    source: AnalysisSource,
    modifier: Modifier = Modifier
) {
    val isReal = source == AnalysisSource.CLOUD_DEMUCS_AI
    val bgColor = if (isReal) RealEmeraldContainer else SampleAmberContainer
    val accentColor = if (isReal) RealEmerald else SampleAmber
    val label = stringResource(
        if (isReal) R.string.badge_real_ai else R.string.badge_sample_preview
    )
    val icon = if (isReal) Icons.Rounded.AutoAwesome else Icons.Rounded.Visibility

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor
        )
    }
}
