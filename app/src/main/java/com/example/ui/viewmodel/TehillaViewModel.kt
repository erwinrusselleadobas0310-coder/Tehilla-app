package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.audio.TehillaAudioController
import com.example.domain.model.SelectedAudioFile
import com.example.domain.model.SongSection
import com.example.domain.model.StemTrack
import com.example.domain.model.StemType
import com.example.domain.model.WorshipRole
import com.example.domain.repository.SongRepository
import com.example.domain.repository.UserRepository
import com.example.ui.state.LibraryFilter
import com.example.ui.state.TehillaUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TehillaViewModel(
    private val songRepository: SongRepository,
    private val userRepository: UserRepository,
    private val audioController: TehillaAudioController
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TehillaUiState(isCloudConfigured = userRepository.isCloudConfigured())
    )
    val uiState: StateFlow<TehillaUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                songRepository.observeSongs(),
                userRepository.observeUserProfile()
            ) { songs, profile ->
                songs to profile
            }.collect { (songs, profile) ->
                _uiState.update { current ->
                    val resolvedActive = current.activeSongId
                        ?.takeIf { id -> songs.any { it.id == id } }
                        ?: songs.firstOrNull()?.id
                    current.copy(
                        songs = songs,
                        activeSongId = resolvedActive,
                        userProfile = profile
                    )
                }
            }
        }
    }

    fun selectActiveSong(songId: String) {
        playbackJob?.cancel()
        audioController.release()

        val targetSong = _uiState.value.songs.firstOrNull { it.id == songId }
        val autoLoop = _uiState.value.userProfile?.autoLoopChorusOnLoad == true
        val chorusSection = if (autoLoop) {
            targetSong?.sections?.firstOrNull { it.name.contains("Chorus", ignoreCase = true) }
        } else null

        if (targetSong?.localAudioUri != null) {
            audioController.loadIfPresent(targetSong.localAudioUri)
        }

        _uiState.update {
            it.copy(
                activeSongId = songId,
                transposeSemitones = 0,
                playerState = it.playerState.copy(
                    currentSeconds = chorusSection?.startSeconds ?: 0,
                    isPlaying = false,
                    isLoopingSection = chorusSection != null,
                    selectedSectionId = chorusSection?.id
                )
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(librarySearchQuery = query) }
    }

    fun updateLibraryFilter(filter: LibraryFilter) {
        _uiState.update { it.copy(libraryFilter = filter) }
    }

    fun deleteSong(songId: String) {
        if (_uiState.value.activeSongId == songId) {
            playbackJob?.cancel()
            audioController.release()
        }
        viewModelScope.launch {
            songRepository.deleteSong(songId)
        }
    }

    // --- Audio Upload ---
    fun onAudioFilePicked(file: SelectedAudioFile) {
        _uiState.update { state ->
            state.copy(
                uploadState = state.uploadState.copy(
                    selectedFile = file,
                    songTitle = file.fileName.substringBeforeLast('.').replace('_', ' '),
                    errorMessage = null,
                    statusMessage = null
                )
            )
        }
    }

    fun onInvalidFileSelected() {
        _uiState.update { state ->
            state.copy(
                uploadState = state.uploadState.copy(
                    errorMessage = "Please select a valid MP3 or WAV audio file."
                )
            )
        }
    }

    fun updateUploadTitle(title: String) {
        _uiState.update { it.copy(uploadState = it.uploadState.copy(songTitle = title)) }
    }

    fun updateUploadArtist(artist: String) {
        _uiState.update { it.copy(uploadState = it.uploadState.copy(artistOrChurch = artist)) }
    }

    fun toggleChordDetection(enabled: Boolean) {
        _uiState.update { it.copy(uploadState = it.uploadState.copy(enableChordDetection = enabled)) }
    }

    fun startAudioAnalysis(onSuccess: (String) -> Unit) {
        val upload = _uiState.value.uploadState
        val file = upload.selectedFile ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    uploadState = it.uploadState.copy(
                        isUploading = true,
                        statusMessage = if (it.isCloudConfigured) {
                            "Uploading to Supabase & separating 4 stems with Demucs AI..."
                        } else {
                            "Generating local preview workspace (Supabase/Demucs offline)..."
                        },
                        errorMessage = null
                    )
                )
            }

            val result = songRepository.submitSongForAnalysis(
                file = file,
                songTitle = upload.songTitle,
                artistName = upload.artistOrChurch,
                enableChordDetection = upload.enableChordDetection
            )

            result.onSuccess { createdSong ->
                audioController.loadIfPresent(createdSong.localAudioUri)
                _uiState.update {
                    it.copy(
                        activeSongId = createdSong.id,
                        transposeSemitones = 0,
                        playerState = it.playerState.copy(currentSeconds = 0, isPlaying = false),
                        uploadState = it.uploadState.copy(
                            isUploading = false,
                            selectedFile = null,
                            songTitle = "",
                            artistOrChurch = "",
                            statusMessage = null
                        )
                    )
                }
                onSuccess(createdSong.id)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        uploadState = it.uploadState.copy(
                            isUploading = false,
                            statusMessage = null,
                            errorMessage = err.message ?: "Upload failed. Please try again."
                        )
                    )
                }
            }
        }
    }

    // --- Player & Sections ---
    fun togglePlayPause() {
        val activeSong = _uiState.value.activeSong ?: return
        val willPlay = !_uiState.value.playerState.isPlaying
        val hasLocalAudio = audioController.loadIfPresent(activeSong.localAudioUri)

        _uiState.update { it.copy(playerState = it.playerState.copy(isPlaying = willPlay)) }
        playbackJob?.cancel()

        if (willPlay) {
            if (hasLocalAudio) {
                audioController.seekToSeconds(_uiState.value.playerState.currentSeconds)
                audioController.play(_uiState.value.playerState.playbackSpeed)
            }
            playbackJob = viewModelScope.launch {
                while (_uiState.value.playerState.isPlaying) {
                    val speed = _uiState.value.playerState.playbackSpeed
                    val delayMs = if (hasLocalAudio) 350L else (1000L / speed).toLong()
                    delay(delayMs)

                    val song = _uiState.value.activeSong ?: break
                    val statePlayer = _uiState.value.playerState
                    val rawNext = if (hasLocalAudio) {
                        audioController.currentPositionSeconds() ?: (statePlayer.currentSeconds + 1)
                    } else {
                        statePlayer.currentSeconds + 1
                    }

                    val loopedSection = if (statePlayer.isLoopingSection) {
                        song.sections.firstOrNull { it.id == statePlayer.selectedSectionId }
                            ?: song.sections.firstOrNull {
                                statePlayer.currentSeconds in it.startSeconds until it.endSeconds
                            }
                    } else null

                    val resolvedNext = when {
                        loopedSection != null && rawNext >= loopedSection.endSeconds -> {
                            if (hasLocalAudio) audioController.seekToSeconds(loopedSection.startSeconds)
                            loopedSection.startSeconds
                        }
                        rawNext >= song.durationSeconds -> {
                            if (hasLocalAudio) audioController.seekToSeconds(0)
                            0
                        }
                        else -> rawNext
                    }

                    _uiState.update { state ->
                        state.copy(
                            playerState = state.playerState.copy(currentSeconds = resolvedNext)
                        )
                    }
                }
            }
        } else {
            audioController.pause()
        }
    }

    fun seekTo(seconds: Int) {
        val max = _uiState.value.activeSong?.durationSeconds ?: 0
        val clamped = seconds.coerceIn(0, max)
        audioController.seekToSeconds(clamped)
        _uiState.update {
            it.copy(playerState = it.playerState.copy(currentSeconds = clamped))
        }
    }

    fun skipSeconds(delta: Int) {
        seekTo(_uiState.value.playerState.currentSeconds + delta)
    }

    fun jumpToSection(section: SongSection) {
        audioController.seekToSeconds(section.startSeconds)
        _uiState.update {
            it.copy(
                playerState = it.playerState.copy(
                    currentSeconds = section.startSeconds,
                    selectedSectionId = section.id
                )
            )
        }
    }

    fun toggleLoopSection() {
        _uiState.update {
            it.copy(playerState = it.playerState.copy(isLoopingSection = !it.playerState.isLoopingSection))
        }
    }

    fun cyclePlaybackSpeed() {
        val nextSpeed = when (_uiState.value.playerState.playbackSpeed) {
            1.0f -> 0.75f
            0.75f -> 0.5f
            else -> 1.0f
        }
        audioController.applySpeed(nextSpeed)
        _uiState.update { it.copy(playerState = it.playerState.copy(playbackSpeed = nextSpeed)) }
    }

    // --- Instrument Studio (4 Stems) ---
    fun updateStemVolume(stemType: StemType, volume: Float) {
        updateActiveSongStems { stems ->
            stems.map { if (it.type == stemType) it.copy(volume = volume, isMuted = false) else it }
        }
    }

    fun toggleStemMute(stemType: StemType) {
        updateActiveSongStems { stems ->
            stems.map { if (it.type == stemType) it.copy(isMuted = !it.isMuted) else it }
        }
    }

    fun toggleStemSolo(stemType: StemType) {
        updateActiveSongStems { stems ->
            val target = stems.firstOrNull { it.type == stemType } ?: return@updateActiveSongStems stems
            val nextSolo = !target.isSoloed
            stems.map {
                if (it.type == stemType) it.copy(isSoloed = nextSolo, isMuted = false)
                else it.copy(isSoloed = false)
            }
        }
    }

    private fun updateActiveSongStems(transform: (List<StemTrack>) -> List<StemTrack>) {
        val activeId = _uiState.value.activeSong?.id ?: return
        _uiState.update { state ->
            state.copy(
                songs = state.songs.map { song ->
                    if (song.id == activeId) song.copy(stems = transform(song.stems)) else song
                }
            )
        }
    }

    // --- Music Insights ---
    fun shiftTranspose(delta: Int) {
        _uiState.update {
            it.copy(transposeSemitones = (it.transposeSemitones + delta).coerceIn(-6, 6))
        }
    }

    // --- Profile & Settings ---
    fun updateWorshipRole(role: WorshipRole) {
        viewModelScope.launch { userRepository.updateProfile { it.copy(role = role) } }
    }

    fun toggleNashvilleNumbers(enabled: Boolean) {
        viewModelScope.launch { userRepository.updateProfile { it.copy(showNashvilleNumbers = enabled) } }
    }

    fun toggleHighQualityWav(enabled: Boolean) {
        viewModelScope.launch { userRepository.updateProfile { it.copy(highQualityWavDownloads = enabled) } }
    }

    fun toggleAutoLoopChorus(enabled: Boolean) {
        viewModelScope.launch { userRepository.updateProfile { it.copy(autoLoopChorusOnLoad = enabled) } }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
        audioController.release()
    }

    companion object {
        fun provideFactory(
            songRepository: SongRepository,
            userRepository: UserRepository,
            audioController: TehillaAudioController
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TehillaViewModel(songRepository, userRepository, audioController) as T
            }
        }
    }
}
