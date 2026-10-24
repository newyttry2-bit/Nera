package com.newyttry2bit.nera.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.ui.component.SongCard

@Composable
fun SearchScreen(
    query: String,
    history: List<String>,
    results: List<VirexSong>,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onSongClick: (VirexSong) -> Unit,
    onSearchHistoryClick: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Search", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text("Songs, artists, playlists") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth()) {
            Text("Search")
        }

        if (history.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Recent", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onClearHistory) { Text("Clear") }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f, fill = false)) {
                items(history, key = { it }) { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AssistChip(onClick = { onSearchHistoryClick(item) }, label = { Text(item) })
                        TextButton(onClick = { onRemoveHistory(item) }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results, key = { it.id }) { song ->
                SongCard(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}
