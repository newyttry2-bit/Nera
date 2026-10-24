package com.newyttry2bit.nera.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.newyttry2bit.nera.data.model.LoopMode
import com.newyttry2bit.nera.data.model.LyricsLine
import com.newyttry2bit.nera.data.model.VirexSong

@Composable
fun FullPlayerSheet(
    currentSong: VirexSong,
    queue: List<VirexSong>,
    lyrics: List<LyricsLine>,
    currentLyricIndex: Int,
    isPlaying: Boolean,
    shuffleEnabled: Boolean,
    loopMode: LoopMode,
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleLoopMode: () -> Unit,
    onPlaySong: (VirexSong) -> Unit,
    onAddToDownloads: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = currentSong.imageUrl,
            contentDescription = currentSong.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Text(currentSong.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(currentSong.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        Slider(
            value = positionMs.toFloat().coerceAtLeast(0f),
            valueRange = 0f..(durationMs.takeIf { it > 0 }?.toFloat() ?: 1f),
            onValueChange = { value -> onSeek(value.toLong()) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "${formatTime(positionMs)} / ${formatTime(durationMs)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShuffle) {
                Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = if (shuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onPrevious) { Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous") }
            IconButton(onClick = onTogglePlayPause) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = "Play Pause") }
            IconButton(onClick = onNext) { Icon(Icons.Rounded.SkipNext, contentDescription = "Next") }
            IconButton(onClick = onCycleLoopMode) {
                Icon(
                    Icons.Rounded.Loop,
                    contentDescription = "Loop",
                    tint = if (loopMode == LoopMode.Off) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onAddToDownloads) { Icon(Icons.Rounded.Download, contentDescription = "Download") }
            IconButton(onClick = { selectedTab = 1 }) { Icon(Icons.Rounded.QueueMusic, contentDescription = "Queue") }
        }

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Lyrics") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Queue") })
        }

        if (selectedTab == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(lyrics) { index, line ->
                    Text(
                        text = line.text,
                        color = if (index == currentLyricIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = if (index == currentLyricIndex) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(queue, key = { _, song -> song.id }) { _, song ->
                    SongCard(song = song, onClick = { onPlaySong(song) })
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000L
    val min = totalSec / 60L
    val sec = totalSec % 60L
    return "%d:%02d".format(min, sec)
}
