package com.example.data.local

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.domain.model.AudioFormat
import com.example.domain.model.SelectedAudioFile
import kotlin.math.roundToInt

object AudioFileHelper {
    fun readAudioMetadata(context: Context, uri: Uri): SelectedAudioFile? {
        val resolver = context.contentResolver
        var fileName = "worship_audio.mp3"
        var sizeBytes = 0L

        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIdx != -1) fileName = cursor.getString(nameIdx) ?: fileName
                if (sizeIdx != -1) sizeBytes = cursor.getLong(sizeIdx)
            }
        }

        val mime = resolver.getType(uri).orEmpty().lowercase()
        val lowerName = fileName.lowercase()
        val format = when {
            lowerName.endsWith(".wav") || mime.contains("wav") -> AudioFormat.WAV
            lowerName.endsWith(".mp3") || mime.contains("mpeg") || mime.contains("mp3") -> AudioFormat.MP3
            else -> return null
        }

        val durationSeconds = extractDurationSeconds(context, uri)
        val sizeMb = ((sizeBytes / (1024.0 * 1024.0)) * 10.0).roundToInt() / 10.0
        return SelectedAudioFile(
            uriString = uri.toString(),
            fileName = fileName,
            format = format,
            sizeMb = if (sizeMb <= 0.0) 4.2 else sizeMb,
            durationSeconds = durationSeconds
        )
    }

    private fun extractDurationSeconds(context: Context, uri: Uri): Int? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            durationMs?.div(1000)?.toInt()?.takeIf { it > 0 }
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }
}
