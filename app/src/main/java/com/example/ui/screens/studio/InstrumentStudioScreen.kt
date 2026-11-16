package com.example.ui.screens.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.components.AnalysisSourceBadge
import com.example.core.components.PlayerTransportBar
import com.example.core.components.WaveformStrip
import com.example.domain.model.StemTrack
import com.example.domain.model.StemType
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.StemBassColor
import com.example.ui.theme.StemDrumsColor
import com.example.ui.theme.StemOtherColor
import com.example.ui.theme.StemVocalsColor
import com.example.ui.theme.TealBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun InstrumentStudioScreen(
    state: TehillaUiState,
    onVolumeChange: (StemType, Float) -> Unit,
    onToggleMute: (StemType) -> Unit,
    onToggleSolo: (StemType) -> Unit,
    onSeek: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onSkipSeconds: (Int) -> Unit
) {
    val song = state.activeSong ?: return
    val progress = state.playerState.currentSeconds.toFloat() / song.durationSeconds.coerceAtLeast(1)
    val anySoloed = song.stems.any { it.isSoloed }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Instrument Studio", style = MaterialTheme.typography.headlineLarge)
                        Text(song.title, style = MaterialTheme.typography.bodyMedium, color = TealBright)
                    }
                    AnalysisSourceBadge(source = song.source)
                }
            }

            item {
                PlayerTransportBar(
                    currentSeconds = state.playerState.currentSeconds,
                    durationSeconds = song.durationSeconds,
                    isPlaying = state.playerState.isPlaying,
                    isLooping = state.playerState.isLoopingSection,
                    playbackSpeed = state.playerState.playbackSpeed,
                    onSeek = onSeek,
                    onTogglePlay = onTogglePlay,
                    onToggleLoop = onToggleLoop,
                    onCycleSpeed = onCycleSpeed,
                    onSkipSeconds = onSkipSeconds
                )
            }

            items(song.stems, key = { it.type.name }) { stem ->
                val effectivelyMuted = stem.isMuted || (anySoloed && !stem.isSoloed)
                StemTrackCard(
                    stem = stem,
                    effectivelyMuted = effectivelyMuted,
                    progressFraction = progress,
                    onVolumeChange = { onVolumeChange(stem.type, it) },
                    onToggleMute = { onToggleMute(stem.type) },
                    onToggleSolo = { onToggleSolo(stem.type) }
                )
            }
        }
    }
}

@Composable
private fun StemTrackCard(
    stem: StemTrack,
    effectivelyMuted: Boolean,
    progressFraction: Float,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleSolo: () -> Unit
) {
    val trackColor = when (stem.type) {
        StemType.VOCALS -> StemVocalsColor
        StemType.DRUMS -> StemDrumsColor
        StemType.BASS -> StemBassColor
        StemType.OTHER -> StemOtherColor
    }
    val icon = when (stem.type) {
        StemType.VOCALS -> Icons.Rounded.Mic
        StemType.DRUMS -> Icons.Rounded.Album
        StemType.BASS -> Icons.Rounded.GraphicEq
        StemType.OTHER -> Icons.Rounded.Piano
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NavySurface)
            .border(
                width = if (stem.isSoloed) 1.5.dp else 1.dp,
                color = if (stem.isSoloed) trackColor else NavyBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
            .testTag("stem_card_${stem.type.name.lowercase()}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(icon, contentDescription = null, tint = trackColor)
                Column {
                    Text(stem.type.displayName, style = MaterialTheme.typography.titleLarge)
                    Text(stem.type.beginnerTip, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StemToggleButton(
                    label = "M",
                    selected = stem.isMuted,
                    activeColor = ErrorCoral,
                    onClick = onToggleMute,
                    modifier = Modifier.testTag("stem_mute_${stem.type.name.lowercase()}")
                )
                StemToggleButton(
                    label = "S",
                    selected = stem.isSoloed,
                    activeColor = trackColor,
                    onClick = onToggleSolo,
                    modifier = Modifier.testTag("stem_solo_${stem.type.name.lowercase()}")
                )
            }
        }

        WaveformStrip(
            samples = stem.waveformSamples,
            activeColor = trackColor,
            progressFraction = progressFraction,
            isMuted = effectivelyMuted
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (effectivelyMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = null,
                tint = if (effectivelyMuted) TextMuted else trackColor,
                modifier = Modifier.size(20.dp)
            )
            Slider(
                value = if (effectivelyMuted) 0f else stem.volume,
                onValueChange = onVolumeChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("stem_slider_${stem.type.name.lowercase()}"),
                colors = SliderDefaults.colors(
                    thumbColor = trackColor,
                    activeTrackColor = trackColor,
                    inactiveTrackColor = NavySurfaceElevated
                )
            )
            Text(
                text = if (effectivelyMuted) "0%" else "${(stem.volume * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StemToggleButton(
    label: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(48.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (selected) activeColor else NavySurfaceElevated,
            contentColor = if (selected) NavyBackground else TextPrimary
        )
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}
