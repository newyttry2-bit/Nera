package com.newyttry2bit.nera.ui.state

import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSong

data class NeraUiState(
    val selectedTab: AppTab = AppTab.Home,
    val categories: List<String> = listOf("Trending", "Punjabi", "Hip Hop", "Bollywood", "Global Hits", "Lofi Chill", "EDM"),
    val selectedCategory: String = "Trending",
    val homeSongs: List<VirexSong> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<VirexSong> = emptyList(),
    val playlists: List<VirexPlaylist> = emptyList(),
    val downloads: List<VirexSong> = emptyList(),
    val currentSong: VirexSong? = null,
    val showSettingsSheet: Boolean = false,
    val isLoading: Boolean = false
)
