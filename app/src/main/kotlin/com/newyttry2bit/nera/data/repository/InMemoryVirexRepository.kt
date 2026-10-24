package com.newyttry2bit.nera.data.repository

import com.newyttry2bit.nera.data.model.LoopMode
import com.newyttry2bit.nera.data.model.LyricsLine
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryVirexRepository : VirexRepository {

    private val songs = listOf(
        VirexSong("1", "Satranga", "Arijit Singh", "https://via.placeholder.com/300", streamUrl = "https://example.com/1.mp3"),
        VirexSong("2", "Lover", "Diljit Dosanjh", "https://via.placeholder.com/300", streamUrl = "https://example.com/2.mp3"),
        VirexSong("3", "Blinding Lights", "The Weeknd", "https://via.placeholder.com/300", streamUrl = "https://example.com/3.mp3"),
        VirexSong("4", "Obsessed", "Riar Saab", "https://via.placeholder.com/300", streamUrl = "https://example.com/4.mp3")
    )

    private val categories = mapOf(
        "Trending" to songs,
        "Punjabi" to songs.reversed(),
        "Hip Hop" to songs.shuffled(),
        "Bollywood" to songs,
        "Global Hits" to songs,
        "Lofi Chill" to songs,
        "EDM" to songs
    )

    private val playlists = MutableStateFlow(
        listOf(
            VirexPlaylist(id = "pl-1", title = "Punjabi Charge", songs = songs.take(3)),
            VirexPlaylist(id = "pl-2", title = "Night Drive", songs = songs.takeLast(2))
        )
    )
    private val downloads = MutableStateFlow(songs.take(2))
    private val history = MutableStateFlow(listOf("Arijit Singh", "Karan Aujla"))
    private val settings = MutableStateFlow(VirexSettings())

    override fun playlistsFlow(): Flow<List<VirexPlaylist>> = playlists.asStateFlow()

    override fun downloadsFlow(): Flow<List<VirexSong>> = downloads.asStateFlow()

    override fun searchHistoryFlow(): Flow<List<String>> = history.asStateFlow()

    override fun settingsFlow(): Flow<VirexSettings> = settings.asStateFlow()

    override suspend fun getCategoryMix(category: String): List<VirexSong> {
        delay(120)
        return categories[category] ?: categories.getValue("Trending")
    }

    override suspend fun searchSongs(query: String): List<VirexSong> {
        delay(80)
        if (query.isBlank()) return emptyList()
        return songs.filter {
            it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getRecommendations(song: VirexSong): List<VirexSong> {
        return songs.filterNot { it.id == song.id }
    }

    override suspend fun getLyrics(song: VirexSong): List<LyricsLine> {
        return listOf(
            LyricsLine(0, "Now playing ${song.title}"),
            LyricsLine(3000, "By ${song.artist}")
        )
    }

    override suspend fun createPlaylist(title: String) {
        if (title.isBlank()) return
        playlists.update { it + VirexPlaylist("pl-${System.currentTimeMillis()}", title, emptyList()) }
    }

    override suspend fun importPlaylist(title: String, lines: List<String>) {
        if (title.isBlank()) return
        val imported = lines.mapNotNull { line -> searchSongs(line).firstOrNull() }
        playlists.update { it + VirexPlaylist("pl-${System.currentTimeMillis()}", title, imported) }
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: VirexSong) {
        playlists.update { current ->
            current.map { pl -> if (pl.id == playlistId) pl.copy(songs = (pl.songs + song).distinctBy { it.id }) else pl }
        }
    }

    override suspend fun removePlaylist(playlistId: String) {
        playlists.update { it.filterNot { pl -> pl.id == playlistId } }
    }

    override suspend fun addToDownloads(song: VirexSong) {
        downloads.update { (it + song).distinctBy { s -> s.id } }
    }

    override suspend fun removeFromDownloads(songId: String) {
        downloads.update { it.filterNot { s -> s.id == songId } }
    }

    override suspend fun addSearchHistory(query: String) {
        if (query.isBlank()) return
        history.update { (listOf(query) + it.filterNot { q -> q.equals(query, true) }).take(20) }
    }

    override suspend fun removeSearchHistory(query: String) {
        history.update { it.filterNot { q -> q.equals(query, true) } }
    }

    override suspend fun clearSearchHistory() {
        history.update { emptyList() }
    }

    override suspend fun updateSettings(settings: VirexSettings) {
        this.settings.value = settings
    }
}
