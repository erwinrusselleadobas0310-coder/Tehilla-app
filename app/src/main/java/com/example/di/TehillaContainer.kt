package com.example.di

import android.content.Context
import com.example.core.audio.TehillaAudioController
import com.example.data.local.UserPreferencesDataStore
import com.example.data.remote.demucs.DemucsApiService
import com.example.data.remote.supabase.SupabaseConfig
import com.example.data.remote.supabase.SupabaseSongDataSource
import com.example.data.repository.SongRepositoryImpl

class TehillaContainer(context: Context) {
    private val appContext = context.applicationContext
    private val supabaseSongDataSource = SupabaseSongDataSource()
    private val demucsApiService = DemucsApiService()
    private val userPreferencesDataStore = UserPreferencesDataStore(
        context = appContext,
        isCloudConfigured = SupabaseConfig.isConfigured() && SupabaseConfig.isDemucsConfigured()
    )

    val audioController = TehillaAudioController(appContext)

    val songRepositoryImpl = SongRepositoryImpl(
        appContext = appContext,
        supabaseSongDataSource = supabaseSongDataSource,
        demucsApiService = demucsApiService,
        userPreferencesDataStore = userPreferencesDataStore
    )
}
