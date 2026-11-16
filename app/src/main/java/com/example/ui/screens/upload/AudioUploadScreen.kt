package com.example.ui.screens.upload

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.AudioFileHelper
import com.example.domain.model.SelectedAudioFile
import com.example.ui.state.UploadUiState
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.RealEmerald
import com.example.ui.theme.RealEmeraldContainer
import com.example.ui.theme.SampleAmber
import com.example.ui.theme.SampleAmberContainer
import com.example.ui.theme.TealBright
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary

@Composable
fun AudioUploadScreen(
    uploadState: UploadUiState,
    isCloudConfigured: Boolean,
    onBack: () -> Unit,
    onFileSelected: (SelectedAudioFile) -> Unit,
    onInvalidFile: () -> Unit,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onToggleChords: (Boolean) -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val context = LocalContext.current

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val metadata = AudioFileHelper.readAudioMetadata(context, uri)
            if (metadata != null) onFileSelected(metadata) else onInvalidFile()
        }
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("upload_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upload Worship Audio", style = MaterialTheme.typography.headlineMedium)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(NavySurface)
                    .border(1.5.dp, TealPrimary.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .clickable(enabled = !uploadState.isUploading) {
                        audioPicker.launch(arrayOf("audio/mpeg", "audio/wav", "audio/x-wav", "audio/*"))
                    }
                    .padding(24.dp)
                    .testTag("audio_file_picker_card"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudUpload,
                    contentDescription = null,
                    tint = TealBright,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = uploadState.selectedFile?.fileName ?: "Tap to Select MP3 or WAV File",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (uploadState.selectedFile != null) {
                        "${uploadState.selectedFile.format.label} • ${uploadState.selectedFile.sizeMb} MB ready"
                    } else {
                        "Supports studio recordings, rehearsal demos, and Sunday mixes (.mp3, .wav)"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = uploadState.songTitle,
                onValueChange = onTitleChange,
                label = { Text("Song Title") },
                placeholder = { Text("e.g., Gratitude (Sunday Rehearsal)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_song_title_input")
            )

            OutlinedTextField(
                value = uploadState.artistOrChurch,
                onValueChange = onArtistChange,
                label = { Text("Artist or Worship Team (Optional)") },
                placeholder = { Text("e.g., Grace Church Worship Team") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_artist_input")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("AI Separation & Insights", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("4-Stem Separation (Demucs)", style = MaterialTheme.typography.bodyLarge)
                        Text("Vocals • Drums • Bass • Other", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = TealBright)
                }

                HorizontalDivider(color = NavyBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Detect BPM, Key & Chords", style = MaterialTheme.typography.bodyLarge)
                        Text("Recommended for worship practice", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = uploadState.enableChordDetection,
                        onCheckedChange = onToggleChords,
                        modifier = Modifier.testTag("upload_chord_detection_switch")
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isCloudConfigured) RealEmeraldContainer else SampleAmberContainer)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCloudConfigured) Icons.Rounded.CloudDone else Icons.Rounded.Science,
                    contentDescription = null,
                    tint = if (isCloudConfigured) RealEmerald else SampleAmber
                )
                Text(
                    text = if (isCloudConfigured) {
                        "Connected to Supabase Storage + Demucs Cloud AI."
                    } else {
                        "Cloud keys not configured yet: upload will create a labeled Local Preview workspace."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            uploadState.errorMessage?.let { err ->
                Text(text = err, color = ErrorCoral, style = MaterialTheme.typography.bodyMedium)
            }

            uploadState.statusMessage?.let { status ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = TealPrimary,
                        trackColor = NavySurfaceElevated
                    )
                    Text(text = status, color = TealBright, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = onAnalyzeClick,
                enabled = uploadState.selectedFile != null && !uploadState.isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("start_analysis_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor = NavyBackground
                )
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uploadState.isUploading) "Processing Audio..." else "Start AI Song Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyBackground
                )
            }
        }
    }
}
