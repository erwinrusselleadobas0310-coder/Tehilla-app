package com.example.domain.model

import com.squareup.moshi.JsonClass

enum class AnalysisSource {
    SAMPLE_PREVIEW,
    CLOUD_DEMUCS_AI
}

enum class ProcessingStatus {
    COMPLETED,
    UPLOADING,
    SEPARATING_STEMS,
    ANALYZING_CHORDS,
    FAILED
}

enum class AudioFormat(val label: String) {
    MP3("MP3"),
    WAV("WAV")
}

enum class StemType(
    val displayName: String,
    val beginnerTip: String
) {
    VOCALS("Vocals", "Mute Vocals to practice leading the song yourself"),
    DRUMS("Drums", "Solo Drums to lock in your worship groove & tempo"),
    BASS("Bass", "Hear the root notes clearly to anchor transitions"),
    OTHER("Other", "Keys, acoustic/electric guitars, pads & strings")
}

@JsonClass(generateAdapter = true)
data class SongSection(
    val id: String,
    val name: String,
    val startSeconds: Int,
    val endSeconds: Int,
    val cueHint: String,
    val colorHex: Long
)

@JsonClass(generateAdapter = true)
data class ChordProgressionItem(
    val id: String,
    val sectionName: String,
    val chords: List<String>,
    val nashvilleNumbers: List<String>,
    val bars: Int
)

@JsonClass(generateAdapter = true)
data class MusicalInsights(
    val bpm: Int,
    val timeSignature: String,
    val musicalKey: String,
    val relativeKey: String,
    val recommendedCapo: String,
    val chordProgressions: List<ChordProgressionItem>
)

data class StemTrack(
    val type: StemType,
    val audioUrl: String?,
    val volume: Float = 0.85f,
    val isMuted: Boolean = false,
    val isSoloed: Boolean = false,
    val waveformSamples: List<Float>
)

data class Song(
    val id: String,
    val title: String,
    val artistOrChurch: String,
    val durationSeconds: Int,
    val format: AudioFormat,
    val fileSizeMb: Double,
    val analyzedDateLabel: String,
    val source: AnalysisSource,
    val status: ProcessingStatus,
    val localAudioUri: String? = null,
    val sections: List<SongSection>,
    val stems: List<StemTrack>,
    val insights: MusicalInsights
)

data class SelectedAudioFile(
    val uriString: String,
    val fileName: String,
    val format: AudioFormat,
    val sizeMb: Double,
    val durationSeconds: Int?
)

enum class WorshipRole(val title: String) {
    WORSHIP_LEADER("Worship Leader"),
    ACOUSTIC_GUITAR("Acoustic Guitar"),
    KEYS_PIANO("Keys / Piano"),
    ELECTRIC_GUITAR("Electric Guitar"),
    BASS("Bass"),
    DRUMS("Drums"),
    VOCALS("Vocalist"),
    AUDIO_TECH("Sound / Media Tech")
}

data class UserProfile(
    val displayName: String,
    val churchName: String,
    val email: String,
    val role: WorshipRole,
    val isSupabaseAuthenticated: Boolean,
    val highQualityWavDownloads: Boolean,
    val showNashvilleNumbers: Boolean,
    val autoLoopChorusOnLoad: Boolean
)
