package com.example.data.remote.supabase

import com.example.domain.model.ChordProgressionItem
import com.example.domain.model.SongSection
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class SupabaseSongRecord(
    val id: String,
    @Json(name = "user_id") val userId: String,
    val title: String,
    @Json(name = "artist_or_church") val artistOrChurch: String,
    @Json(name = "audio_storage_path") val audioStoragePath: String,
    val format: String,
    @Json(name = "duration_seconds") val durationSeconds: Int,
    @Json(name = "file_size_mb") val fileSizeMb: Double,
    val status: String,
    val bpm: Int? = null,
    @Json(name = "musical_key") val musicalKey: String? = null,
    @Json(name = "vocals_url") val vocalsUrl: String? = null,
    @Json(name = "drums_url") val drumsUrl: String? = null,
    @Json(name = "bass_url") val bassUrl: String? = null,
    @Json(name = "other_url") val otherUrl: String? = null,
    val sections: List<SongSection> = emptyList(),
    @Json(name = "chord_progressions") val chordProgressions: List<ChordProgressionItem> = emptyList()
)

interface SupabaseRestApi {
    @POST("storage/v1/object/worship-audio/{objectPath}")
    suspend fun uploadAudioFile(
        @Path(value = "objectPath", encoded = true) objectPath: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authBearer: String,
        @Header("x-upsert") upsert: String = "true",
        @Body body: RequestBody
    )

    @GET("rest/v1/songs")
    suspend fun fetchUserSongs(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authBearer: String,
        @Query("user_id") userIdFilter: String
    ): List<SupabaseSongRecord>

    @POST("rest/v1/songs")
    suspend fun upsertSongRecord(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authBearer: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body record: SupabaseSongRecord
    )

    @DELETE("rest/v1/songs")
    suspend fun deleteSongRecord(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authBearer: String,
        @Query("id") idFilter: String
    )
}

/**
 * Retrofit data source for Supabase Storage ("worship-audio" bucket) and PostgREST ("songs" table).
 */
class SupabaseSongDataSource {

    private val api: SupabaseRestApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val baseUrl = if (SupabaseConfig.supabaseUrl.startsWith("http")) {
            "${SupabaseConfig.supabaseUrl}/"
        } else {
            "https://your-project.supabase.co/"
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SupabaseRestApi::class.java)
    }

    suspend fun uploadAudioBytes(
        objectPath: String,
        bytes: ByteArray,
        mimeType: String
    ): String {
        val key = SupabaseConfig.supabaseAnonKey
        val requestBody = bytes.toRequestBody(mimeType.toMediaType())
        api.uploadAudioFile(
            objectPath = objectPath,
            apiKey = key,
            authBearer = "Bearer $key",
            body = requestBody
        )
        return "${SupabaseConfig.supabaseUrl}/storage/v1/object/public/worship-audio/$objectPath"
    }

    suspend fun fetchUserSongs(userId: String): List<SupabaseSongRecord> {
        val key = SupabaseConfig.supabaseAnonKey
        return api.fetchUserSongs(
            apiKey = key,
            authBearer = "Bearer $key",
            userIdFilter = "eq.$userId"
        )
    }

    suspend fun upsertSongRecord(record: SupabaseSongRecord) {
        val key = SupabaseConfig.supabaseAnonKey
        api.upsertSongRecord(
            apiKey = key,
            authBearer = "Bearer $key",
            record = record
        )
    }

    suspend fun deleteSongRecord(songId: String) {
        val key = SupabaseConfig.supabaseAnonKey
        api.deleteSongRecord(
            apiKey = key,
            authBearer = "Bearer $key",
            idFilter = "eq.$songId"
        )
    }
}
