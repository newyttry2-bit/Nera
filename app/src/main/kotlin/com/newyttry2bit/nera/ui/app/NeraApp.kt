package com.newyttry2bit.nera.ui.app

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.ui.component.MiniPlayer
import com.newyttry2bit.nera.ui.screen.DownloadsScreen
import com.newyttry2bit.nera.ui.screen.HomeScreen
import com.newyttry2bit.nera.ui.screen.PlaylistsScreen
import com.newyttry2bit.nera.ui.screen.SearchScreen
import com.newyttry2bit.nera.ui.viewmodel.NeraViewModel
import kotlinx.coroutines.launch

@Composable
fun NeraApp(viewModel: NeraViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var visualizerEnabled by remember { mutableStateOf(true) }
    var hqAudioEnabled by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                state.currentSong?.let {
                    MiniPlayer(song = it, onOpenPlayer = {})
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
            onSongClick = viewModel::setCurrentSong,
            onRefresh = viewModel::refreshHome,
            onQueryChanged = viewModel::updateSearchQuery,
            onSearch = viewModel::search,
            onCreatePlaylist = viewModel::createPlaylist,
            onOpenSettings = {
                scope.launch {
                    viewModel.setSettingsVisibility(true)
                    sheetState.show()
                }
            }
        )

        if (state.showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setSettingsVisibility(false) },
                sheetState = sheetState
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(text = "VirexDL Settings")
                    SettingRow(title = "Audio Visualizer", checked = visualizerEnabled) {
                        visualizerEnabled = it
                    }
                    SettingRow(title = "HQ 320kbps Preferred", checked = hqAudioEnabled) {
                        hqAudioEnabled = it
                    }
                    TextButton(onClick = { scope.launch { sheetState.hide(); viewModel.setSettingsVisibility(false) } }) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBody(
    state: com.newyttry2bit.nera.ui.state.NeraUiState,
    paddingValues: PaddingValues,
    onCategorySelected: (String) -> Unit,
    onSongClick: (com.newyttry2bit.nera.data.model.VirexSong) -> Unit,
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onOpenSettings: () -> Unit
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
                onSongClick = onSongClick,
                onRefresh = onRefresh,
                onOpenSettings = onOpenSettings
            )

            AppTab.Search -> SearchScreen(
                query = state.searchQuery,
                results = state.searchResults,
                onQueryChanged = onQueryChanged,
                onSearch = onSearch,
                onSongClick = onSongClick
            )

            AppTab.Playlists -> PlaylistsScreen(
                playlists = state.playlists,
                onSongClick = onSongClick,
                onCreatePlaylist = onCreatePlaylist
            )

            AppTab.Downloads -> DownloadsScreen(
                songs = state.downloads,
                onSongClick = onSongClick
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
