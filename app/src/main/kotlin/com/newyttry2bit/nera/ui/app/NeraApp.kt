package com.newyttry2bit.nera.ui.app

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.ui.component.FullPlayerSheet
import com.newyttry2bit.nera.ui.component.MiniPlayer
import com.newyttry2bit.nera.ui.screen.DownloadsScreen
import com.newyttry2bit.nera.ui.screen.HomeScreen
import com.newyttry2bit.nera.ui.screen.PlaylistDetailScreen
import com.newyttry2bit.nera.ui.screen.PlaylistsScreen
import com.newyttry2bit.nera.ui.screen.SearchScreen
import com.newyttry2bit.nera.ui.state.NeraUiState
import com.newyttry2bit.nera.ui.viewmodel.NeraViewModel
import kotlinx.coroutines.launch

@Composable
fun NeraApp() {
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
    val viewModel: NeraViewModel = viewModel(factory = NeraViewModel.factory(application))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val playerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                state.currentSong?.let { song ->
                    MiniPlayer(
                        song = song,
                        isPlaying = state.isPlaying,
                        onOpenPlayer = {
                            viewModel.showFullPlayer(true)
                            scope.launch { playerSheetState.show() }
                        },
                        onTogglePlayPause = viewModel::togglePlayPause
                    )
                }
                NavigationBar {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = state.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        AppTab.Home -> Icons.Rounded.Home
                                        AppTab.Search -> Icons.Rounded.Search
                                        AppTab.Playlists -> Icons.Rounded.LibraryMusic
                                        AppTab.Downloads -> Icons.Rounded.Download
                                    },
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppBody(
            state = state,
            paddingValues = innerPadding,
            onCategorySelected = viewModel::selectCategory,
            onPlaySong = viewModel::playSong,
            onRefresh = viewModel::refreshHome,
            onQueryChanged = viewModel::updateSearchQuery,
            onSearch = { viewModel.search() },
            onSearchHistoryClick = { query -> viewModel.search(query) },
            onRemoveHistory = viewModel::removeSearchHistory,
            onClearHistory = viewModel::clearSearchHistory,
            onCreatePlaylist = viewModel::createPlaylist,
            onOpenPlaylist = viewModel::openPlaylist,
            onDeletePlaylist = viewModel::deletePlaylist,
            onPlaylistBack = viewModel::closePlaylist,
            onImportTitleChange = viewModel::updateImportTitle,
            onImportTextChange = viewModel::updateImportText,
            onImportPlaylist = viewModel::importPlaylist,
            onPlayDownloads = viewModel::playDownloads,
            onRemoveDownload = viewModel::removeDownload,
            onOpenSettings = {
                viewModel.showSettings(true)
                scope.launch { settingsSheetState.show() }
            },
            onPlayRecommendations = viewModel::playRecommendationsFromCurrent
        )

        if (state.showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showSettings(false) },
                sheetState = settingsSheetState
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(text = "VirexDL Settings")
                    SettingRow(
                        title = "Audio Visualizer",
                        checked = state.settings.visualizerEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateSettings { it.copy(visualizerEnabled = checked) }
                        }
                    )
                    SettingRow(
                        title = "HQ 320kbps Preferred",
                        checked = state.settings.hqAudioEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateSettings { it.copy(hqAudioEnabled = checked) }
                        }
                    )
                    SettingRow(
                        title = "Crossfade",
                        checked = state.settings.crossfadeEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateSettings { it.copy(crossfadeEnabled = checked) }
                        }
                    )
                    TextButton(onClick = {
                        scope.launch {
                            settingsSheetState.hide()
                            viewModel.showSettings(false)
                        }
                    }) {
                        Text("Close")
                    }
                }
            }
        }

        if (state.showFullPlayer && state.currentSong != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showFullPlayer(false) },
                sheetState = playerSheetState
            ) {
                FullPlayerSheet(
                    currentSong = state.currentSong!!,
                    queue = state.queue,
                    lyrics = state.lyrics,
                    currentLyricIndex = state.currentLyricIndex,
                    isPlaying = state.isPlaying,
                    shuffleEnabled = state.shuffleEnabled,
                    loopMode = state.loopMode,
                    positionMs = state.positionMs,
                    durationMs = state.durationMs,
                    onSeek = viewModel::seekTo,
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onNext = viewModel::playNext,
                    onPrevious = viewModel::playPrevious,
                    onToggleShuffle = viewModel::toggleShuffle,
                    onCycleLoopMode = viewModel::cycleLoopMode,
                    onPlaySong = { song -> viewModel.playSong(song, state.queue) },
                    onAddToDownloads = viewModel::addCurrentToDownloads
                )
            }
        }
    }
}

@Composable
private fun AppBody(
    state: NeraUiState,
    paddingValues: PaddingValues,
    onCategorySelected: (String) -> Unit,
    onPlaySong: (com.newyttry2bit.nera.data.model.VirexSong, List<com.newyttry2bit.nera.data.model.VirexSong>) -> Unit,
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onSearchHistoryClick: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onClearHistory: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onOpenPlaylist: (String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onPlaylistBack: () -> Unit,
    onImportTitleChange: (String) -> Unit,
    onImportTextChange: (String) -> Unit,
    onImportPlaylist: () -> Unit,
    onPlayDownloads: () -> Unit,
    onRemoveDownload: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onPlayRecommendations: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        when (state.selectedTab) {
            AppTab.Home -> HomeScreen(
                state = state,
                onCategorySelected = onCategorySelected,
                onSongClick = onPlaySong,
                onRefresh = onRefresh,
                onOpenSettings = onOpenSettings,
                onPlayRecommendations = onPlayRecommendations
            )

            AppTab.Search -> SearchScreen(
                query = state.searchQuery,
                history = state.searchHistory,
                results = state.searchResults,
                onQueryChanged = onQueryChanged,
                onSearch = onSearch,
                onSongClick = { song -> onPlaySong(song, state.searchResults) },
                onSearchHistoryClick = onSearchHistoryClick,
                onRemoveHistory = onRemoveHistory,
                onClearHistory = onClearHistory
            )

            AppTab.Playlists -> {
                val playlist = state.selectedPlaylist
                if (playlist == null) {
                    PlaylistsScreen(
                        playlists = state.playlists,
                        importTitle = state.importTitle,
                        importText = state.importText,
                        onCreatePlaylist = onCreatePlaylist,
                        onOpenPlaylist = onOpenPlaylist,
                        onDeletePlaylist = onDeletePlaylist,
                        onImportTitleChange = onImportTitleChange,
                        onImportTextChange = onImportTextChange,
                        onImportPlaylist = onImportPlaylist
                    )
                } else {
                    PlaylistDetailScreen(
                        playlist = playlist,
                        onBack = onPlaylistBack,
                        onPlaySong = onPlaySong
                    )
                }
            }

            AppTab.Downloads -> DownloadsScreen(
                songs = state.downloads,
                onSongClick = { song -> onPlaySong(song, state.downloads) },
                onPlayAll = onPlayDownloads,
                onRemove = onRemoveDownload
            )
        }
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
