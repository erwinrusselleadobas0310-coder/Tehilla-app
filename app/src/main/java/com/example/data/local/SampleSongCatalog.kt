package com.example.data.local

import com.example.domain.model.AnalysisSource
import com.example.domain.model.AudioFormat
import com.example.domain.model.ChordProgressionItem
import com.example.domain.model.MusicalInsights
import com.example.domain.model.ProcessingStatus
import com.example.domain.model.Song
import com.example.domain.model.SongSection
import com.example.domain.model.StemTrack
import com.example.domain.model.StemType
import kotlin.math.abs
import kotlin.math.sin

/**
 * Provides realistic worship music sample data strictly for initial UI previews.
 * Every item is explicitly marked with [AnalysisSource.SAMPLE_PREVIEW].
 */
object SampleSongCatalog {

    private fun generateWaveform(seed: Int, count: Int = 36): List<Float> =
        List(count) { idx ->
            val raw = (abs(sin((idx + seed * 3) * 0.45)) * 0.7 + 0.25).toFloat()
            raw.coerceIn(0.15f, 1.0f)
        }

    fun getInitialSamples(): List<Song> = listOf(
        Song(
            id = "sample-way-maker",
            title = "Way Maker (Sunday Acoustic)",
            artistOrChurch = "Grace Fellowship Worship",
            durationSeconds = 264,
            format = AudioFormat.MP3,
            fileSizeMb = 6.2,
            analyzedDateLabel = "Sample Preview",
            source = AnalysisSource.SAMPLE_PREVIEW,
            status = ProcessingStatus.COMPLETED,
            sections = listOf(
                SongSection("s1", "Intro", 0, 24, "Warm pad & piano swells", 0xFF38BDF8),
                SongSection("s2", "Verse 1", 24, 68, "Soft vocals, acoustic rhythm", 0xFF14B8A6),
                SongSection("s3", "Chorus", 68, 120, "Full band entrance on beat 1", 0xFF2DD4BF),
                SongSection("s4", "Verse 2", 120, 162, "Add light snare & bass groove", 0xFF14B8A6),
                SongSection("s5", "Bridge", 162, 224, "Dynamic build — \"Even when I don't see it\"", 0xFFF472B6),
                SongSection("s6", "Outro", 224, 264, "Drop to keys & congregation", 0xFFA78BFA)
            ),
            stems = StemType.entries.mapIndexed { index, type ->
                StemTrack(
                    type = type,
                    audioUrl = null,
                    waveformSamples = generateWaveform(seed = index + 1)
                )
            },
            insights = MusicalInsights(
                bpm = 68,
                timeSignature = "4/4",
                musicalKey = "E Major",
                relativeKey = "C# Minor",
                recommendedCapo = "Capo 2 (Play D shapes) or Capo 4 (Play C shapes)",
                chordProgressions = listOf(
                    ChordProgressionItem(
                        id = "cp1",
                        sectionName = "Verse & Chorus",
                        chords = listOf("A2", "E", "B", "C#m7"),
                        nashvilleNumbers = listOf("4", "1", "5", "6m"),
                        bars = 4
                    ),
                    ChordProgressionItem(
                        id = "cp2",
                        sectionName = "Bridge",
                        chords = listOf("A2", "E/G#", "B", "C#m7"),
                        nashvilleNumbers = listOf("4", "1/3", "5", "6m"),
                        bars = 4
                    )
                )
            )
        ),
        Song(
            id = "sample-goodness-of-god",
            title = "Goodness of God (Rehearsal Mix)",
            artistOrChurch = "Youth Worship Team",
            durationSeconds = 292,
            format = AudioFormat.WAV,
            fileSizeMb = 48.5,
            analyzedDateLabel = "Sample Preview",
            source = AnalysisSource.SAMPLE_PREVIEW,
            status = ProcessingStatus.COMPLETED,
            sections = listOf(
                SongSection("g1", "Intro", 0, 20, "Solo piano 4 bars", 0xFF38BDF8),
                SongSection("g2", "Verse 1", 20, 64, "Lead vocal + light keys", 0xFF14B8A6),
                SongSection("g3", "Chorus", 64, 114, "All instruments, big tom fill", 0xFF2DD4BF),
                SongSection("g4", "Bridge", 114, 198, "\"Your goodness is running after\"", 0xFFF472B6),
                SongSection("g5", "Chorus Out", 198, 292, "Sing a cappella on final refrain", 0xFFA78BFA)
            ),
            stems = StemType.entries.mapIndexed { index, type ->
                StemTrack(
                    type = type,
                    audioUrl = null,
                    waveformSamples = generateWaveform(seed = index + 5)
                )
            },
            insights = MusicalInsights(
                bpm = 63,
                timeSignature = "4/4",
                musicalKey = "G Major",
                relativeKey = "E Minor",
                recommendedCapo = "Open G (No Capo) or Capo 2 (Play F shapes for Ab)",
                chordProgressions = listOf(
                    ChordProgressionItem(
                        id = "gp1",
                        sectionName = "Verse",
                        chords = listOf("G", "C", "G", "D/F#"),
                        nashvilleNumbers = listOf("1", "4", "1", "5/7"),
                        bars = 4
                    ),
                    ChordProgressionItem(
                        id = "gp2",
                        sectionName = "Chorus",
                        chords = listOf("C", "G", "C", "G", "Em7", "C", "D", "G"),
                        nashvilleNumbers = listOf("4", "1", "4", "1", "6m", "4", "5", "1"),
                        bars = 8
                    )
                )
            )
        ),
        Song(
            id = "sample-10000-reasons",
            title = "10,000 Reasons (Bless the Lord)",
            artistOrChurch = "Sunday Morning Choir",
            durationSeconds = 248,
            format = AudioFormat.MP3,
            fileSizeMb = 5.8,
            analyzedDateLabel = "Sample Preview",
            source = AnalysisSource.SAMPLE_PREVIEW,
            status = ProcessingStatus.COMPLETED,
            sections = listOf(
                SongSection("r1", "Chorus Intro", 0, 32, "Warm acoustic & pad", 0xFF2DD4BF),
                SongSection("r2", "Verse 1", 32, 76, "Steady quarter-note pulse", 0xFF14B8A6),
                SongSection("r3", "Verse 2", 76, 122, "Add harmony vocals", 0xFF38BDF8),
                SongSection("r4", "Verse 3", 122, 174, "Build into final chorus", 0xFFF472B6),
                SongSection("r5", "Final Chorus", 174, 248, "Tag \"Worship His holy name\" 2x", 0xFFA78BFA)
            ),
            stems = StemType.entries.mapIndexed { index, type ->
                StemTrack(
                    type = type,
                    audioUrl = null,
                    waveformSamples = generateWaveform(seed = index + 9)
                )
            },
            insights = MusicalInsights(
                bpm = 73,
                timeSignature = "4/4",
                musicalKey = "G Major",
                relativeKey = "E Minor",
                recommendedCapo = "Open G (No Capo)",
                chordProgressions = listOf(
                    ChordProgressionItem(
                        id = "rp1",
                        sectionName = "Chorus & Verses",
                        chords = listOf("C", "G", "D/F#", "Em7"),
                        nashvilleNumbers = listOf("4", "1", "5/7", "6m"),
                        bars = 4
                    )
                )
            )
        )
    )
}
