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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSong

@Composable
fun PlaylistsScreen(
    playlists: List<VirexPlaylist>,
    onSongClick: (VirexSong) -> Unit,
    onCreatePlaylist: (String) -> Unit
) {
    val (title, setTitle) = remember { mutableStateOf("My Playlist") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Playlists", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onCreatePlaylist(title) }) {
                Text("Create")
            }
            Button(onClick = { setTitle("Imported Mix") }) {
                Text("Import")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(playlists, key = { it.id }) { playlist ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(playlist.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${playlist.songs.size} songs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        playlist.songs.take(2).forEach { song ->
                            Text(
                                text = "• ${song.title} — ${song.artist}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (playlist.songs.isNotEmpty()) {
                            Button(onClick = { onSongClick(playlist.songs.first()) }) {
                                Text("Play")
                            }
                        }
                    }
                }
            }
        }
    }
}
