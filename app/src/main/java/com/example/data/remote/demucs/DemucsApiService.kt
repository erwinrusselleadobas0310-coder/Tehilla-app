package com.example.data.remote.demucs

import com.example.data.remote.supabase.SupabaseConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class DemucsSeparationRequest(
    @Json(name = "song_id") val songId: String,
    @Json(name = "audio_url") val audioUrl: String,
    val model: String = "htdemucs",
    @Json(name = "analyze_bpm_and_chords") val analyzeBpmAndChords: Boolean = true
)

@JsonClass(generateAdapter = true)
data class DemucsJobResponse(
    @Json(name = "job_id") val jobId: String,
    val status: String,
    @Json(name = "vocals_url") val vocalsUrl: String? = null,
    @Json(name = "drums_url") val drumsUrl: String? = null,
    @Json(name = "bass_url") val bassUrl: String? = null,
    @Json(name = "other_url") val otherUrl: String? = null,
    val bpm: Int? = null,
    @Json(name = "musical_key") val musicalKey: String? = null,
    @Json(name = "error_message") val errorMessage: String? = null
)

interface DemucsRetrofitApi {
    @POST("separations")
    suspend fun submitSeparationJob(@Body request: DemucsSeparationRequest): DemucsJobResponse

    @GET("separations/{jobId}")
    suspend fun getJobStatus(@Path("jobId") jobId: String): DemucsJobResponse
}

/**
 * Cloud Demucs AI 4-stem separation HTTP client (Vocals, Drums, Bass, Other).
 */
class DemucsApiService {
    private val api: DemucsRetrofitApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val baseUrl = if (SupabaseConfig.demucsBaseUrl.startsWith("http")) {
            SupabaseConfig.demucsBaseUrl
        } else {
            "https://api.tehilla-audio.example.com/v1/"
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(DemucsRetrofitApi::class.java)
    }

    suspend fun submitSeparationJob(request: DemucsSeparationRequest): DemucsJobResponse {
        return api.submitSeparationJob(request)
    }

    suspend fun getJobStatus(jobId: String): DemucsJobResponse {
        return api.getJobStatus(jobId)
    }
}
