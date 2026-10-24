package com.newyttry2bit.nera.data.model

data class VirexSong(
    val id: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val durationSeconds: Int = 0
)

data class VirexPlaylist(
    val id: String,
    val title: String,
    val songs: List<VirexSong>
)

enum class AppTab(val label: String) {
    Home("Home"),
    Search("Search"),
    Playlists("Playlists"),
    Downloads("Downloads")
}
