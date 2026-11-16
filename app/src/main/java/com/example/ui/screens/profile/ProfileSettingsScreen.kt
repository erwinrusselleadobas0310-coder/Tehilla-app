package com.example.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.WorshipRole
import com.example.ui.state.TehillaUiState
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.RealEmerald
import com.example.ui.theme.SampleAmber
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealContainer

@Composable
fun ProfileSettingsScreen(
    state: TehillaUiState,
    onSelectRole: (WorshipRole) -> Unit,
    onToggleNashville: (Boolean) -> Unit,
    onToggleWavDownloads: (Boolean) -> Unit,
    onToggleAutoLoop: (Boolean) -> Unit
) {
    val profile = state.userProfile ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Profile & Settings", style = MaterialTheme.typography.headlineLarge)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_musician_avatar),
                    contentDescription = "Worship musician avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, TealBright, CircleShape)
                )
                Column {
                    Text(profile.displayName, style = MaterialTheme.typography.titleLarge)
                    Text(profile.churchName, style = MaterialTheme.typography.bodyMedium, color = TealBright)
                    Text(profile.email, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("My Worship Team Role", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorshipRole.entries.forEach { role ->
                        FilterChip(
                            selected = profile.role == role,
                            onClick = { onSelectRole(role) },
                            label = { Text(role.title) },
                            modifier = Modifier.testTag("role_chip_${role.name}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealContainer,
                                selectedLabelColor = TealBright
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (state.isCloudConfigured) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff,
                        contentDescription = null,
                        tint = if (state.isCloudConfigured) RealEmerald else SampleAmber
                    )
                    Text("Supabase & Demucs AI Cloud", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = if (state.isCloudConfigured) {
                        "Connected. Uploaded MP3/WAV tracks are stored in Supabase and separated via Demucs AI."
                    } else {
                        "Demo Mode. Set SUPABASE_URL, SUPABASE_ANON_KEY, and DEMUCS_API_BASE_URL in AI Studio Secrets to enable cloud stem separation."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Practice Preferences", style = MaterialTheme.typography.titleMedium)

                SettingSwitchRow(
                    title = "Show Nashville Numbers",
                    subtitle = "Display 1 - 4 - 5 - 6m under chord cards",
                    checked = profile.showNashvilleNumbers,
                    onCheckedChange = onToggleNashville,
                    testTag = "switch_nashville_numbers"
                )
                HorizontalDivider(color = NavyBorder)
                SettingSwitchRow(
                    title = "High-Quality Stem Audio (WAV)",
                    subtitle = "Best clarity for studio headphones",
                    checked = profile.highQualityWavDownloads,
                    onCheckedChange = onToggleWavDownloads,
                    testTag = "switch_high_quality_wav"
                )
                HorizontalDivider(color = NavyBorder)
                SettingSwitchRow(
                    title = "Auto-Loop Chorus on Load",
                    subtitle = "Start rehearsal focused on the chorus",
                    checked = profile.autoLoopChorusOnLoad,
                    onCheckedChange = onToggleAutoLoop,
                    testTag = "switch_auto_loop_chorus"
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
