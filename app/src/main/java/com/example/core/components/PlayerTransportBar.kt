package com.example.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerTransportBar(
    currentSeconds: Int,
    durationSeconds: Int,
    isPlaying: Boolean,
    isLooping: Boolean,
    playbackSpeed: Float,
    onSeek: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onSkipSeconds: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Slider(
            value = currentSeconds.toFloat().coerceIn(0f, durationSeconds.coerceAtLeast(1).toFloat()),
            onValueChange = { onSeek(it.toInt()) },
            valueRange = 0f..durationSeconds.coerceAtLeast(1).toFloat(),
            modifier = Modifier.testTag("player_seek_slider"),
            colors = SliderDefaults.colors(
                thumbColor = TealBright,
                activeTrackColor = TealPrimary,
                inactiveTrackColor = NavySurfaceElevated
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentSeconds),
                style = MaterialTheme.typography.labelSmall,
                color = TealBright
            )
            Text(
                text = formatTime(durationSeconds),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = onCycleSpeed,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("player_speed_button")
            ) {
                Text(
                    text = "${playbackSpeed}x",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (playbackSpeed != 1.0f) TealBright else TextSecondary
                )
            }

            IconButton(
                onClick = { onSkipSeconds(-10) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("player_rewind_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay10,
                    contentDescription = "Back 10 seconds",
                    tint = TextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }

            FilledIconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(60.dp)
                    .testTag("player_play_pause_button"),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = TealPrimary,
                    contentColor = NavyBackground
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(34.dp)
                )
            }

            IconButton(
                onClick = { onSkipSeconds(10) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("player_forward_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Forward10,
                    contentDescription = "Forward 10 seconds",
                    tint = TextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(
                onClick = onToggleLoop,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("player_loop_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Repeat,
                    contentDescription = "Loop section",
                    tint = if (isLooping) TealBright else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "%d:%02d".format(mins, secs)
}
