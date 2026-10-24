package com.newyttry2bit.nera.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VirexSong(
    val id: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val durationSeconds: Int = 0,
    val streamUrl: String? = null,
    val downloadUrl: String? = null
)

@Serializable
data class VirexPlaylist(
    val id: String,
    val title: String,
    val songs: List<VirexSong>
)

@Serializable
data class LyricsLine(
    val timestampMs: Long,
    val text: String
)

@Serializable
data class VirexSettings(
    val visualizerEnabled: Boolean = true,
    val hqAudioEnabled: Boolean = true,
    val crossfadeEnabled: Boolean = false,
    val themeColorHex: String = "#1DB954"
)

enum class AppTab(val label: String) {
    Home("Home"),
    Search("Search"),
    Playlists("Playlists"),
    Downloads("Downloads")
}

enum class LoopMode {
    Off,
    One,
    All
}
