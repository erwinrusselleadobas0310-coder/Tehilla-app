package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.UserProfile
import com.example.domain.model.WorshipRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tehillaPrefs: DataStore<Preferences> by preferencesDataStore(name = "tehilla_user_prefs")

class UserPreferencesDataStore(
    private val context: Context,
    private val isCloudConfigured: Boolean
) {
    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val CHURCH_NAME = stringPreferencesKey("church_name")
        val EMAIL = stringPreferencesKey("email")
        val ROLE = stringPreferencesKey("worship_role")
        val HIGH_QUALITY_WAV = booleanPreferencesKey("high_quality_wav")
        val SHOW_NASHVILLE = booleanPreferencesKey("show_nashville_numbers")
        val AUTO_LOOP_CHORUS = booleanPreferencesKey("auto_loop_chorus")
    }

    fun observeProfile(): Flow<UserProfile> = context.tehillaPrefs.data.map { prefs ->
        val roleName = prefs[Keys.ROLE] ?: WorshipRole.WORSHIP_LEADER.name
        val resolvedRole = WorshipRole.entries.firstOrNull { it.name == roleName } ?: WorshipRole.WORSHIP_LEADER

        UserProfile(
            displayName = prefs[Keys.DISPLAY_NAME] ?: "David Reyes",
            churchName = prefs[Keys.CHURCH_NAME] ?: "New Hope Worship Team",
            email = prefs[Keys.EMAIL] ?: "david.worship@example.org",
            role = resolvedRole,
            isSupabaseAuthenticated = isCloudConfigured,
            highQualityWavDownloads = prefs[Keys.HIGH_QUALITY_WAV] ?: true,
            showNashvilleNumbers = prefs[Keys.SHOW_NASHVILLE] ?: true,
            autoLoopChorusOnLoad = prefs[Keys.AUTO_LOOP_CHORUS] ?: false
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.tehillaPrefs.edit { prefs ->
            prefs[Keys.DISPLAY_NAME] = profile.displayName
            prefs[Keys.CHURCH_NAME] = profile.churchName
            prefs[Keys.EMAIL] = profile.email
            prefs[Keys.ROLE] = profile.role.name
            prefs[Keys.HIGH_QUALITY_WAV] = profile.highQualityWavDownloads
            prefs[Keys.SHOW_NASHVILLE] = profile.showNashvilleNumbers
            prefs[Keys.AUTO_LOOP_CHORUS] = profile.autoLoopChorusOnLoad
        }
    }
}
