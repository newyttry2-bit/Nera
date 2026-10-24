package com.newyttry2bit.nera.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.ui.component.SongCard
import com.newyttry2bit.nera.ui.state.NeraUiState

@Composable
fun HomeScreen(
    state: NeraUiState,
    onCategorySelected: (String) -> Unit,
    onSongClick: (VirexSong, List<VirexSong>) -> Unit,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
    onPlayRecommendations: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("VirexDL", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.categories.forEach { category ->
                val selected = category == state.selectedCategory
                AssistChip(
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${state.selectedCategory} Mix", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onRefresh) {
                Text("Shuffle Mix")
            }
        }

        Button(onClick = onPlayRecommendations, modifier = Modifier.fillMaxWidth()) {
            Text("Smart Queue from Current Song")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        state.homeSongs.forEach { song ->
            SongCard(song = song, onClick = { onSongClick(song, state.homeSongs) })
        }
    }
}
