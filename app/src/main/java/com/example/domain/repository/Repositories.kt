package com.example.domain.repository

import com.example.domain.model.SelectedAudioFile
import com.example.domain.model.Song
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(): Flow<List<Song>>
    suspend fun submitSongForAnalysis(
        file: SelectedAudioFile,
        songTitle: String,
        artistName: String,
        enableChordDetection: Boolean
    ): Result<Song>
    suspend fun deleteSong(songId: String)
}

interface UserRepository {
    fun observeUserProfile(): Flow<UserProfile>
    suspend fun updateProfile(transform: (UserProfile) -> UserProfile)
    fun isCloudConfigured(): Boolean
}
