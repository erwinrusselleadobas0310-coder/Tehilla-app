package com.example.ui.state

import com.example.domain.model.AnalysisSource
import com.example.domain.model.AudioFormat
import com.example.domain.model.SelectedAudioFile
import com.example.domain.model.Song
import com.example.domain.model.UserProfile

enum class LibraryFilter(val label: String) {
    ALL("All Songs"),
    SAMPLES("Sample Previews"),
    REAL_AI("Real AI"),
    MP3("MP3"),
    WAV("WAV")
}

data class UploadUiState(
    val selectedFile: SelectedAudioFile? = null,
    val songTitle: String = "",
    val artistOrChurch: String = "",
    val enableChordDetection: Boolean = true,
    val isUploading: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentSeconds: Int = 42,
    val isLoopingSection: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val selectedSectionId: String? = null
)

data class TehillaUiState(
    val songs: List<Song> = emptyList(),
    val activeSongId: String? = null,
    val librarySearchQuery: String = "",
    val libraryFilter: LibraryFilter = LibraryFilter.ALL,
    val uploadState: UploadUiState = UploadUiState(),
    val playerState: PlayerUiState = PlayerUiState(),
    val transposeSemitones: Int = 0,
    val userProfile: UserProfile? = null,
    val isCloudConfigured: Boolean = false
) {
    val activeSong: Song?
        get() = songs.firstOrNull { it.id == activeSongId } ?: songs.firstOrNull()

    val filteredSongs: List<Song>
        get() = songs.filter { song ->
            val matchesQuery = librarySearchQuery.isBlank() ||
                song.title.contains(librarySearchQuery, ignoreCase = true) ||
                song.artistOrChurch.contains(librarySearchQuery, ignoreCase = true)

            val matchesFilter = when (libraryFilter) {
                LibraryFilter.ALL -> true
                LibraryFilter.SAMPLES -> song.source == AnalysisSource.SAMPLE_PREVIEW
                LibraryFilter.REAL_AI -> song.source == AnalysisSource.CLOUD_DEMUCS_AI
                LibraryFilter.MP3 -> song.format == AudioFormat.MP3
                LibraryFilter.WAV -> song.format == AudioFormat.WAV
            }
            matchesQuery && matchesFilter
        }
}
