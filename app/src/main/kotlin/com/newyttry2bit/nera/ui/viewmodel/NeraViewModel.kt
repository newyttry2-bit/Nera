package com.newyttry2bit.nera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.data.repository.InMemoryVirexRepository
import com.newyttry2bit.nera.data.repository.VirexRepository
import com.newyttry2bit.nera.ui.state.NeraUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NeraViewModel(
    private val repository: VirexRepository = InMemoryVirexRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NeraUiState())
    val uiState: StateFlow<NeraUiState> = _uiState.asStateFlow()

    init {
        refreshHome()
        loadLibrarySections()
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshHome()
    }

    fun refreshHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val songs = repository.getCategoryMix(_uiState.value.selectedCategory)
            _uiState.update { it.copy(homeSongs = songs, isLoading = false) }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        viewModelScope.launch {
            val results = repository.searchSongs(_uiState.value.searchQuery)
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    fun setCurrentSong(song: VirexSong) {
        _uiState.update { it.copy(currentSong = song) }
    }

    fun setSettingsVisibility(visible: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = visible) }
    }

    fun createPlaylist(title: String) {
        if (title.isBlank()) return
        val playlist = VirexPlaylist(
            id = "local-${System.currentTimeMillis()}",
            title = title,
            songs = emptyList()
        )
        _uiState.update { it.copy(playlists = it.playlists + playlist) }
    }

    private fun loadLibrarySections() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    playlists = repository.getPlaylists(),
                    downloads = repository.getDownloads()
                )
            }
        }
    }
}
