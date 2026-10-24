package com.newyttry2bit.nera.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.ui.component.SongCard

@Composable
fun PlaylistDetailScreen(
    playlist: VirexPlaylist,
    onBack: () -> Unit,
    onPlaySong: (com.newyttry2bit.nera.data.model.VirexSong, List<com.newyttry2bit.nera.data.model.VirexSong>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        Text(playlist.title, style = MaterialTheme.typography.headlineSmall)
        Text("${playlist.songs.size} songs", style = MaterialTheme.typography.bodyMedium)

        Button(
            onClick = { playlist.songs.firstOrNull()?.let { onPlaySong(it, playlist.songs) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play Playlist")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(playlist.songs, key = { it.id }) { song ->
                SongCard(song = song, onClick = { onPlaySong(song, playlist.songs) })
            }
        }
    }
}
