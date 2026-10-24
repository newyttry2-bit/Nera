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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.ui.component.SongCard

@Composable
fun SearchScreen(
    query: String,
    results: List<VirexSong>,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onSongClick: (VirexSong) -> Unit
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results, key = { it.id }) { song ->
                SongCard(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}
