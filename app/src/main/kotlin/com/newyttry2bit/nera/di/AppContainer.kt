package com.newyttry2bit.nera.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.newyttry2bit.nera.data.local.LocalStore
import com.newyttry2bit.nera.data.network.LyricsApiService
import com.newyttry2bit.nera.data.network.SaavnApiService
import com.newyttry2bit.nera.data.repository.NetworkContracts
import com.newyttry2bit.nera.data.repository.RealVirexRepository
import com.newyttry2bit.nera.data.repository.VirexRepository
import com.newyttry2bit.nera.player.PlaybackController
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class AppContainer(context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private fun retrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val saavnApi = retrofit(NetworkContracts.SAAVN_BASE_URL).create(SaavnApiService::class.java)
    private val lyricsApi = retrofit(NetworkContracts.LYRICS_BASE_URL).create(LyricsApiService::class.java)
    private val localStore = LocalStore(context)

    val repository: VirexRepository = RealVirexRepository(saavnApi, lyricsApi, localStore)
    val playbackController: PlaybackController = PlaybackController(context)
}
