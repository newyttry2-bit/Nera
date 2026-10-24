package com.newyttry2bit.nera.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexPlaylist

@Composable
fun PlaylistsScreen(
    playlists: List<VirexPlaylist>,
    importTitle: String,
    importText: String,
    onCreatePlaylist: (String) -> Unit,
    onOpenPlaylist: (String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onImportTitleChange: (String) -> Unit,
    onImportTextChange: (String) -> Unit,
    onImportPlaylist: () -> Unit
) {
    val (newTitle, setNewTitle) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Playlists", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = newTitle,
            onValueChange = setNewTitle,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("New playlist name") }
        )
        Button(onClick = { onCreatePlaylist(newTitle); setNewTitle("") }, modifier = Modifier.fillMaxWidth()) {
            Text("Create Playlist")
        }

        OutlinedTextField(
            value = importTitle,
            onValueChange = onImportTitleChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Import playlist title") }
        )
        OutlinedTextField(
            value = importText,
            onValueChange = onImportTextChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            label = { Text("Paste songs (one per line)") }
        )
        Button(onClick = onImportPlaylist, modifier = Modifier.fillMaxWidth()) {
            Text("Import Playlist")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(playlists, key = { it.id }) { playlist ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(playlist.title, style = MaterialTheme.typography.titleMedium)
                        Text("${playlist.songs.size} songs", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onOpenPlaylist(playlist.id) }) { Text("Open") }
                            Button(onClick = { onDeletePlaylist(playlist.id) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}
