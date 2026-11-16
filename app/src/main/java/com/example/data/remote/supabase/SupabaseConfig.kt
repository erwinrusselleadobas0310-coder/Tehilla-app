package com.example.data.remote.supabase

import com.example.BuildConfig

object SupabaseConfig {
    val supabaseUrl: String
        get() = BuildConfig.SUPABASE_URL.trimEnd('/')

    val supabaseAnonKey: String
        get() = BuildConfig.SUPABASE_ANON_KEY

    val demucsBaseUrl: String
        get() = BuildConfig.DEMUCS_API_BASE_URL.trimEnd('/') + "/"

    fun isConfigured(): Boolean =
        supabaseUrl.isNotBlank() &&
            !supabaseUrl.contains("your-project") &&
            supabaseAnonKey.isNotBlank() &&
            !supabaseAnonKey.contains("your-supabase-anon-key")

    fun isDemucsConfigured(): Boolean =
        demucsBaseUrl.isNotBlank() &&
            !demucsBaseUrl.contains("tehilla-audio.example.com")
}
