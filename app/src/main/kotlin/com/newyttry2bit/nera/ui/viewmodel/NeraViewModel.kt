package com.newyttry2bit.nera.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.data.model.LoopMode
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.data.repository.VirexRepository
import com.newyttry2bit.nera.di.AppContainer
import com.newyttry2bit.nera.player.PlaybackController
import com.newyttry2bit.nera.ui.state.NeraUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NeraViewModel(
    application: Application,
    private val repository: VirexRepository,
    private val playbackController: PlaybackController
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NeraUiState())
    val uiState: StateFlow<NeraUiState> = _uiState.asStateFlow()

    private var lyricsJob: Job? = null

    init {
        observeLocalData()
        observePlayback()
        refreshHome()
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab, selectedPlaylistId = if (tab == AppTab.Playlists) it.selectedPlaylistId else null) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshHome()
    }

    fun refreshHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.getCategoryMix(_uiState.value.selectedCategory)
            }.onSuccess { songs ->
                _uiState.update { it.copy(homeSongs = songs, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Failed to load home mix") }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search(query: String = _uiState.value.searchQuery) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, searchQuery = query) }
            runCatching {
                repository.searchSongs(query)
            }.onSuccess { results ->
                repository.addSearchHistory(query)
                _uiState.update { it.copy(searchResults = results, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Search failed") }
            }
        }
    }

    fun removeSearchHistory(query: String) {
        viewModelScope.launch { repository.removeSearchHistory(query) }
    }

    fun clearSearchHistory() {
        viewModelScope.launch { repository.clearSearchHistory() }
    }

    fun createPlaylist(title: String) {
        viewModelScope.launch { repository.createPlaylist(title) }
    }

    fun updateImportTitle(value: String) {
        _uiState.update { it.copy(importTitle = value) }
    }

    fun updateImportText(value: String) {
        _uiState.update { it.copy(importText = value) }
    }

    fun importPlaylist() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val lines = state.importText.lines()
            runCatching {
                repository.importPlaylist(state.importTitle, lines)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, importText = "", importTitle = "") }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Import failed") }
            }
        }
    }

    fun openPlaylist(playlistId: String) {
        _uiState.update { it.copy(selectedPlaylistId = playlistId) }
    }

    fun closePlaylist() {
        _uiState.update { it.copy(selectedPlaylistId = null) }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch { repository.removePlaylist(playlistId) }
    }

    fun playSong(song: VirexSong, queue: List<VirexSong>) {
        val index = queue.indexOfFirst { it.id == song.id }.takeIf { it >= 0 } ?: 0
        playbackController.setQueue(queue = queue, index = index, autoPlay = true)
        _uiState.update { it.copy(showFullPlayer = true) }
        fetchLyrics(song)
    }

    fun playDownloads() {
        val downloads = _uiState.value.downloads
        if (downloads.isNotEmpty()) {
            playbackController.setQueue(downloads, index = 0, autoPlay = true)
            _uiState.update { it.copy(showFullPlayer = true) }
            fetchLyrics(downloads.first())
        }
    }

    fun playRecommendationsFromCurrent() {
        val current = _uiState.value.currentSong ?: return
        viewModelScope.launch {
            runCatching {
                repository.getRecommendations(current)
            }.onSuccess { recs ->
                if (recs.isNotEmpty()) {
                    playbackController.setQueue(recs, index = 0, autoPlay = true)
                    fetchLyrics(recs.first())
                }
            }
        }
    }

    fun togglePlayPause() = playbackController.togglePlayPause()

    fun seekTo(positionMs: Long) = playbackController.seekTo(positionMs)

    fun playNext() {
        playbackController.playNext()
        _uiState.value.currentSong?.let(::fetchLyrics)
    }

    fun playPrevious() {
        playbackController.playPrevious()
        _uiState.value.currentSong?.let(::fetchLyrics)
    }

    fun toggleShuffle() {
        playbackController.setShuffle(!_uiState.value.shuffleEnabled)
    }

    fun cycleLoopMode() {
        val next = when (_uiState.value.loopMode) {
            LoopMode.Off -> LoopMode.All
            LoopMode.All -> LoopMode.One
            LoopMode.One -> LoopMode.Off
        }
        playbackController.setLoopMode(next)
    }

    fun showSettings(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }

    fun showFullPlayer(show: Boolean) {
        _uiState.update { it.copy(showFullPlayer = show) }
    }

    fun addCurrentToDownloads() {
        val current = _uiState.value.currentSong ?: return
        viewModelScope.launch { repository.addToDownloads(current) }
    }

    fun removeDownload(songId: String) {
        viewModelScope.launch { repository.removeFromDownloads(songId) }
    }

    fun updateSettings(transform: (VirexSettings) -> VirexSettings) {
        viewModelScope.launch {
            val updated = transform(_uiState.value.settings)
            repository.updateSettings(updated)
        }
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            repository.playlistsFlow().collect { playlists ->
                _uiState.update { it.copy(playlists = playlists) }
            }
        }
        viewModelScope.launch {
            repository.downloadsFlow().collect { downloads ->
                _uiState.update { it.copy(downloads = downloads) }
            }
        }
        viewModelScope.launch {
            repository.searchHistoryFlow().collect { history ->
                _uiState.update { it.copy(searchHistory = history) }
            }
        }
        viewModelScope.launch {
            repository.settingsFlow().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    private fun observePlayback() {
        viewModelScope.launch {
            playbackController.state.collect { playback ->
                _uiState.update {
                    val currentSong = playback.currentSong
                    it.copy(
                        queue = playback.queue,
                        currentIndex = playback.currentIndex,
                        currentSong = currentSong,
                        isPlaying = playback.isPlaying,
                        positionMs = playback.positionMs,
                        durationMs = playback.durationMs,
                        shuffleEnabled = playback.shuffleEnabled,
                        loopMode = playback.loopMode,
                        currentLyricIndex = it.lyrics.indexOfLast { line -> line.timestampMs <= playback.positionMs }
                    )
                }
            }
        }
    }

    private fun fetchLyrics(song: VirexSong) {
        lyricsJob?.cancel()
        lyricsJob = viewModelScope.launch {
            runCatching { repository.getLyrics(song) }
                .onSuccess { lines -> _uiState.update { it.copy(lyrics = lines) } }
                .onFailure { _uiState.update { it.copy(lyrics = emptyList()) } }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.release()
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val container = AppContainer(application.applicationContext)
                return NeraViewModel(
                    application = application,
                    repository = container.repository,
                    playbackController = container.playbackController
                ) as T
            }
        }
    }
}
