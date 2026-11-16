package com.example.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.components.AnalysisSourceBadge
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicInsightsScreen(
    state: TehillaUiState,
    onTranspose: (Int) -> Unit
) {
    val song = state.activeSong ?: return
    val insights = song.insights
    val showNashville = state.userProfile?.showNashvilleNumbers ?: true
    val transposedKey = transposeKeyLabel(insights.musicalKey, state.transposeSemitones)

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Music Insights", style = MaterialTheme.typography.headlineLarge)
                        Text(song.title, style = MaterialTheme.typography.bodyMedium, color = TealBright)
                    }
                    AnalysisSourceBadge(source = song.source)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InsightMetricCard(
                        label = "TEMPO",
                        value = "${insights.bpm} BPM",
                        helper = "${insights.timeSignature} Time",
                        icon = Icons.Rounded.Speed,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("insights_bpm_card")
                    )
                    InsightMetricCard(
                        label = "MUSICAL KEY",
                        value = transposedKey,
                        helper = "Rel: ${insights.relativeKey}",
                        icon = Icons.Rounded.MusicNote,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("insights_key_card")
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(TealContainer)
                        .border(1.dp, TealPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = TealBright)
                    Column {
                        Text("Worship Team Tip", style = MaterialTheme.typography.titleMedium, color = TealBright)
                        Text(insights.recommendedCapo, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(NavySurface)
                        .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transpose Chords", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (state.transposeSemitones == 0) {
                                "Original Key"
                            } else {
                                val sign = if (state.transposeSemitones > 0) "+" else ""
                                "$sign${state.transposeSemitones} semitones"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TealBright
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { onTranspose(-1) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("transpose_down_button")
                        ) {
                            Icon(Icons.Rounded.Remove, contentDescription = "Transpose down")
                        }
                        FilledTonalIconButton(
                            onClick = { onTranspose(1) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("transpose_up_button")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Transpose up")
                        }
                    }
                }
            }

            item {
                Text("Chord Progressions", style = MaterialTheme.typography.titleLarge)
            }

            items(insights.chordProgressions, key = { it.id }) { progression ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(NavySurface)
                        .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(progression.sectionName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${progression.bars} bars",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        progression.chords.forEachIndexed { idx, chord ->
                            val displayChord = transposeChordSymbol(chord, state.transposeSemitones)
                            val number = progression.nashvilleNumbers.getOrNull(idx)
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NavySurfaceElevated)
                                    .border(1.dp, TealPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = displayChord,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TealBright
                                )
                                if (showNashville && number != null) {
                                    Text(
                                        text = number,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightMetricCard(
    label: String,
    value: String,
    helper: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = TealBright, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Text(value, style = MaterialTheme.typography.headlineMedium)
        Text(helper, style = MaterialTheme.typography.bodyMedium, color = TealBright)
    }
}

private val CHROMATIC_NOTES = listOf("C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B")

private fun transposeKeyLabel(originalKey: String, semitones: Int): String {
    if (semitones == 0) return originalKey
    val root = originalKey.substringBefore(" ")
    val quality = originalKey.substringAfter(" ", "")
    return "${transposeRoot(root, semitones)} $quality".trim()
}

private fun transposeChordSymbol(chord: String, semitones: Int): String {
    if (semitones == 0) return chord
    if (chord.contains("/")) {
        val parts = chord.split("/")
        return "${transposeChordSymbol(parts[0], semitones)}/${transposeChordSymbol(parts[1], semitones)}"
    }
    val twoCharRoot = chord.take(2)
    val rootLen = if (twoCharRoot.endsWith("#") || twoCharRoot.endsWith("b")) 2 else 1
    val root = chord.take(rootLen)
    val suffix = chord.drop(rootLen)
    return transposeRoot(root, semitones) + suffix
}

private fun transposeRoot(root: String, semitones: Int): String {
    val normalized = when (root) {
        "Db" -> "C#"
        "D#" -> "Eb"
        "Gb" -> "F#"
        "G#" -> "Ab"
        "A#" -> "Bb"
        else -> root
    }
    val idx = CHROMATIC_NOTES.indexOf(normalized)
    if (idx == -1) return root
    val shifted = (idx + semitones + CHROMATIC_NOTES.size * 2) % CHROMATIC_NOTES.size
    return CHROMATIC_NOTES[shifted]
}
