package com.newyttry2bit.nera.ui.state

import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.data.model.LoopMode
import com.newyttry2bit.nera.data.model.LyricsLine
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong

data class NeraUiState(
    val selectedTab: AppTab = AppTab.Home,
    val categories: List<String> = listOf("Trending", "Punjabi", "Hip Hop", "Bollywood", "Global Hits", "Lofi Chill", "EDM"),
    val selectedCategory: String = "Trending",
    val homeSongs: List<VirexSong> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<VirexSong> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val playlists: List<VirexPlaylist> = emptyList(),
    val selectedPlaylistId: String? = null,
    val downloads: List<VirexSong> = emptyList(),
    val queue: List<VirexSong> = emptyList(),
    val currentIndex: Int = 0,
    val currentSong: VirexSong? = null,
    val lyrics: List<LyricsLine> = emptyList(),
    val currentLyricIndex: Int = -1,
    val showSettingsSheet: Boolean = false,
    val showFullPlayer: Boolean = false,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val loopMode: LoopMode = LoopMode.Off,
    val settings: VirexSettings = VirexSettings(),
    val importTitle: String = "",
    val importText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedPlaylist: VirexPlaylist?
        get() = playlists.firstOrNull { it.id == selectedPlaylistId }
}
