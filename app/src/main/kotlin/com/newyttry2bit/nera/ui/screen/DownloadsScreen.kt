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
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.ui.component.SongCard

@Composable
fun DownloadsScreen(
    songs: List<VirexSong>,
    onSongClick: (VirexSong) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Downloads", style = MaterialTheme.typography.headlineSmall)
        Text("${songs.size} tracks available offline", style = MaterialTheme.typography.bodyMedium)

        Button(onClick = { songs.firstOrNull()?.let(onSongClick) }, modifier = Modifier.fillMaxWidth()) {
            Text("Play All")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(songs, key = { it.id }) { song ->
                SongCard(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}
