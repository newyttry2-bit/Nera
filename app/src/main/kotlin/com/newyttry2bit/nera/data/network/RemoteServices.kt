package com.newyttry2bit.nera.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SaavnApiService {
    @GET("api/search/songs")
    suspend fun searchSongs(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): SearchSongsResponse

    @GET
    suspend fun getSuggestions(@Url url: String): SearchSongsResponse
}

@Serializable
data class SearchSongsResponse(
    val data: SearchSongsData? = null
)

@Serializable
data class SearchSongsData(
    val results: List<SongItemDto> = emptyList()
)

@Serializable
data class SongItemDto(
    val id: String? = null,
    val name: String? = null,
    val duration: String? = null,
    val image: List<ImageDto>? = null,
    val artists: ArtistsDto? = null,
    val downloadUrl: List<ImageDto>? = null
)

@Serializable

data class ImageDto(
    val quality: String? = null,
    val url: String? = null
)

@Serializable

data class ArtistsDto(
    val primary: List<ArtistDto>? = null
)

@Serializable

data class ArtistDto(
    val name: String? = null
)

interface LyricsApiService {
    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String
    ): JsonElement

    @GET("api/search")
    suspend fun searchLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String
    ): JsonElement
}
