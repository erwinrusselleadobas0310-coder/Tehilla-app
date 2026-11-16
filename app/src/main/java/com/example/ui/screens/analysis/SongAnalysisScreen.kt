package com.example.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.core.components.formatTime
import com.example.domain.model.SongSection
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SongAnalysisScreen(
    state: TehillaUiState,
    onBack: () -> Unit,
    onOpenStudio: () -> Unit,
    onOpenInsights: () -> Unit,
    onSeek: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onSkipSeconds: (Int) -> Unit,
    onSectionSelected: (SongSection) -> Unit
) {
    val song = state.activeSong ?: return

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("analysis_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    AnalysisSourceBadge(source = song.source)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "${song.artistOrChurch} • ${song.insights.bpm} BPM • Key of ${song.insights.musicalKey}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TealBright
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(NavySurface)
                        .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Visual Song Map", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        song.sections.forEach { section ->
                            val weight = (section.endSeconds - section.startSeconds).coerceAtLeast(1).toFloat()
                            val isCurrent = state.playerState.currentSeconds in section.startSeconds until section.endSeconds
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight()
                                    .background(
                                        Color(section.colorHex).copy(alpha = if (isCurrent) 0.95f else 0.42f)
                                    )
                                    .clickable { onSectionSelected(section) }
                                    .testTag("timeline_block_${section.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = section.name.take(5),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NavyBackground
                                )
                            }
                        }
                    }
                    Text(
                        text = "Tap any colored block or section below to jump straight to that part.",
                        style = MaterialTheme.typography.bodyMedium
                    )
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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onOpenStudio,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("analysis_open_studio_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = NavyBackground
                        )
                    ) {
                        Icon(Icons.Rounded.Tune, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Instrument Studio", color = NavyBackground)
                    }
                    OutlinedButton(
                        onClick = onOpenInsights,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("analysis_open_insights_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = TealBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chords & Key", color = TealBright)
                    }
                }
            }

            item {
                Text("Song Sections", style = MaterialTheme.typography.titleLarge)
            }

            items(song.sections, key = { it.id }) { section ->
                val isCurrent = state.playerState.currentSeconds in section.startSeconds until section.endSeconds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCurrent) NavySurfaceElevated else NavySurface)
                        .border(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) TealBright else NavyBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSectionSelected(section) }
                        .padding(16.dp)
                        .testTag("section_row_${section.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(section.colorHex))
                        )
                        Column {
                            Text(section.name, style = MaterialTheme.typography.titleMedium)
                            Text(section.cueHint, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text(
                        text = "${formatTime(section.startSeconds)} - ${formatTime(section.endSeconds)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) TealBright else TextSecondary
                    )
                }
            }
        }
    }
}
