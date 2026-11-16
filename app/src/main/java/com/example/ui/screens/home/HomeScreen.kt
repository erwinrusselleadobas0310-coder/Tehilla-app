package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.components.AnalysisSourceBadge
import com.example.core.components.formatTime
import com.example.domain.model.Song
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SampleAmber
import com.example.ui.theme.SampleAmberContainer
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    state: TehillaUiState,
    onAnalyzeNewSongClick: () -> Unit,
    onOpenSongAnalysis: (String) -> Unit,
    onOpenStudio: () -> Unit,
    onOpenInsights: () -> Unit
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
    val userName = state.userProfile?.displayName?.substringBefore(" ") ?: "Worship Team"

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.studio_tagline),
                        style = MaterialTheme.typography.labelSmall,
                        color = TealBright
                    )
                    Text(
                        text = "$greeting, $userName",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Primary Hero CTA Card with atmospheric studio banner artwork
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, TealPrimary.copy(alpha = 0.55f), RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_worship_studio),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        NavyBackground.copy(alpha = 0.92f),
                                        TealContainer.copy(alpha = 0.72f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.GraphicEq,
                                contentDescription = null,
                                tint = TealBright,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Separate & Analyze Audio",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = "Upload any MP3 or WAV worship track to isolate Vocals, Drums, Bass, and Keys—plus detect BPM, Key, and Chords.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary.copy(alpha = 0.88f)
                        )
                        Button(
                            onClick = onAnalyzeNewSongClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("analyze_new_song_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealPrimary,
                                contentColor = NavyBackground
                            )
                        ) {
                            Icon(Icons.Rounded.AddCircleOutline, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.cta_analyze_new_song),
                                style = MaterialTheme.typography.titleMedium,
                                color = NavyBackground
                            )
                        }
                    }
                }
            }

            state.activeSong?.let { active ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionTile(
                            title = "4-Stem Studio",
                            subtitle = "Mute or Solo parts",
                            icon = Icons.Rounded.Tune,
                            onClick = onOpenStudio,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_quick_studio_tile")
                        )
                        QuickActionTile(
                            title = "Key & Chords",
                            subtitle = "${active.insights.bpm} BPM • ${active.insights.musicalKey}",
                            icon = Icons.Rounded.MusicNote,
                            onClick = onOpenInsights,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_quick_insights_tile")
                        )
                    }
                }
            }

            if (!state.isCloudConfigured) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SampleAmberContainer)
                            .border(1.dp, SampleAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = SampleAmber
                        )
                        Text(
                            text = "Preview Mode Active: Songs marked SAMPLE PREVIEW use built-in worship examples until Supabase & Demucs cloud keys are connected.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Ready for Rehearsal",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(state.songs.take(3), key = { it.id }) { song ->
                HomeSongCard(
                    song = song,
                    onClick = { onOpenSongAnalysis(song.id) }
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(18.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TealBright)
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun HomeSongCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("home_song_card_${song.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalysisSourceBadge(source = song.source)
            Text(
                text = "${song.format.label} • ${formatTime(song.durationSeconds)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Text(text = song.title, style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${song.insights.bpm} BPM • Key of ${song.insights.musicalKey}",
                style = MaterialTheme.typography.bodyMedium,
                color = TealBright
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "Open song analysis",
                tint = TextSecondary
            )
        }
    }
}
