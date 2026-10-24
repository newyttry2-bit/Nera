package com.newyttry2bit.nera.data.repository

import com.newyttry2bit.nera.data.local.LocalStore
import com.newyttry2bit.nera.data.model.LyricsLine
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong
import com.newyttry2bit.nera.data.network.LyricsApiService
import com.newyttry2bit.nera.data.network.SaavnApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RealVirexRepository(
    private val saavnApiService: SaavnApiService,
    private val lyricsApiService: LyricsApiService,
    private val localStore: LocalStore
) : VirexRepository {

    private val categoryPools = mapOf(
        "Trending" to listOf("Arijit Singh", "Diljit Dosanjh", "Karan Aujla", "The Weeknd", "Taylor Swift"),
        "Punjabi" to listOf("Karan Aujla", "Diljit Dosanjh", "Sidhu Moose Wala", "AP Dhillon", "Shubh"),
        "Hip Hop" to listOf("Kendrick Lamar", "Drake", "Travis Scott", "Divine", "Eminem"),
        "Bollywood" to listOf("Arijit Singh", "Pritam", "Atif Aslam", "Shreya Ghoshal", "KK"),
        "Global Hits" to listOf("The Weeknd", "Taylor Swift", "Dua Lipa", "Bruno Mars", "Ed Sheeran"),
        "Lofi Chill" to listOf("Lofi Sleep", "Indian Lofi", "Chillhop", "Aesthetic Beats"),
        "EDM" to listOf("Martin Garrix", "Alan Walker", "Avicii", "David Guetta")
    )

    override fun playlistsFlow(): Flow<List<VirexPlaylist>> = localStore.playlistsFlow

    override fun downloadsFlow(): Flow<List<VirexSong>> = localStore.downloadsFlow

    override fun searchHistoryFlow(): Flow<List<String>> = localStore.searchHistoryFlow

    override fun settingsFlow(): Flow<VirexSettings> = localStore.settingsFlow

    override suspend fun getCategoryMix(category: String): List<VirexSong> {
        val topics = (categoryPools[category] ?: categoryPools.getValue("Trending")).shuffled().take(3)
        val combined = mutableListOf<VirexSong>()
        topics.forEach { topic ->
            val response = saavnApiService.searchSongs(topic, limit = 12)
            combined += response.data?.results.orEmpty().mapNotNull { it.toSong() }
        }
        return combined.shuffled().distinctBy { it.id }.take(20)
    }

    override suspend fun searchSongs(query: String): List<VirexSong> {
        if (query.isBlank()) return emptyList()
        val response = saavnApiService.searchSongs(query = query, limit = 25)
        return response.data?.results.orEmpty().mapNotNull { it.toSong() }
    }

    override suspend fun getRecommendations(song: VirexSong): List<VirexSong> {
        val response = saavnApiService.getSuggestions("api/songs/${song.id}/suggestions?limit=20")
        return response.data?.results.orEmpty().mapNotNull { it.toSong() }
    }

    override suspend fun getLyrics(song: VirexSong): List<LyricsLine> {
        val result = runCatching {
            lyricsApiService.getLyrics(trackName = song.title, artistName = song.artist)
        }.getOrElse {
            lyricsApiService.searchLyrics(trackName = song.title, artistName = song.artist)
        }

        val root = result.toJsonObjectOrNull() ?: return emptyList()
        val synced = root["syncedLyrics"]?.jsonPrimitive?.contentOrNull
        if (!synced.isNullOrBlank()) return parseLrc(synced)

        val plain = root["plainLyrics"]?.jsonPrimitive?.contentOrNull
        if (!plain.isNullOrBlank()) {
            return plain.lines().filter { it.isNotBlank() }.mapIndexed { index, line ->
                LyricsLine(timestampMs = index * 3_000L, text = line.trim())
            }
        }

        val asArray = result as? JsonArray
        val first = asArray?.firstOrNull()?.jsonObject
        val fallback = first?.get("syncedLyrics")?.jsonPrimitive?.contentOrNull
            ?: first?.get("plainLyrics")?.jsonPrimitive?.contentOrNull
        return fallback?.let { value ->
            if (value.contains("[")) parseLrc(value)
            else value.lines().filter { it.isNotBlank() }.mapIndexed { idx, line -> LyricsLine(idx * 3_000L, line) }
        }.orEmpty()
    }

    override suspend fun createPlaylist(title: String) {
        if (title.isBlank()) return
        val existing = localStore.playlistsFlow.first()
        val playlist = VirexPlaylist(
            id = "pl-${System.currentTimeMillis()}",
            title = title.trim(),
            songs = emptyList()
        )
        localStore.setPlaylists(existing + playlist)
    }

    override suspend fun importPlaylist(title: String, lines: List<String>) {
        if (title.isBlank()) return
        val songs = lines.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(400)
            .mapNotNull { entry -> runCatching { searchSongs(entry).firstOrNull() }.getOrNull() }
            .toList()

        val existing = localStore.playlistsFlow.first()
        val playlist = VirexPlaylist(
            id = "pl-${System.currentTimeMillis()}",
            title = title.trim(),
            songs = songs
        )
        localStore.setPlaylists(existing + playlist)
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: VirexSong) {
        val updated = localStore.playlistsFlow.first().map { playlist ->
            if (playlist.id != playlistId) playlist
            else playlist.copy(songs = (playlist.songs + song).distinctBy { it.id })
        }
        localStore.setPlaylists(updated)
    }

    override suspend fun removePlaylist(playlistId: String) {
        localStore.setPlaylists(localStore.playlistsFlow.first().filterNot { it.id == playlistId })
    }

    override suspend fun addToDownloads(song: VirexSong) {
        val updated = (localStore.downloadsFlow.first() + song).distinctBy { it.id }
        localStore.setDownloads(updated)
    }

    override suspend fun removeFromDownloads(songId: String) {
        localStore.setDownloads(localStore.downloadsFlow.first().filterNot { it.id == songId })
    }

    override suspend fun addSearchHistory(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) return
        val next = (listOf(clean) + localStore.searchHistoryFlow.first().filterNot { it.equals(clean, true) }).take(20)
        localStore.setSearchHistory(next)
    }

    override suspend fun removeSearchHistory(query: String) {
        localStore.setSearchHistory(localStore.searchHistoryFlow.first().filterNot { it.equals(query, true) })
    }

    override suspend fun clearSearchHistory() {
        localStore.setSearchHistory(emptyList())
    }

    override suspend fun updateSettings(settings: VirexSettings) {
        localStore.setSettings(settings)
    }

    private fun parseLrc(raw: String): List<LyricsLine> {
        return raw.lines().mapNotNull { line ->
            val match = Regex("""\[(\d{2}):(\d{2})(?:\.(\d{1,2}))?\](.*)""").find(line.trim()) ?: return@mapNotNull null
            val min = match.groupValues[1].toLongOrNull() ?: return@mapNotNull null
            val sec = match.groupValues[2].toLongOrNull() ?: return@mapNotNull null
            val cent = match.groupValues[3].toLongOrNull() ?: 0L
            val text = match.groupValues[4].trim()
            val ms = min * 60_000L + sec * 1_000L + cent * 10L
            LyricsLine(timestampMs = ms, text = text)
        }.ifEmpty { emptyList() }
    }
}

private fun kotlinx.serialization.json.JsonElement.toJsonObjectOrNull(): JsonObject? = runCatching {
    jsonObject
}.getOrNull()

private fun com.newyttry2bit.nera.data.network.SongItemDto.toSong(): VirexSong? {
    val songId = id ?: return null
    val songName = name ?: return null
    val artist = artists?.primary?.firstOrNull()?.name ?: "Unknown Artist"
    val imageUrl = image?.lastOrNull()?.url ?: "https://via.placeholder.com/300"
    val stream = downloadUrl?.lastOrNull()?.url
    return VirexSong(
        id = songId,
        title = songName,
        artist = artist,
        imageUrl = imageUrl,
        durationSeconds = duration?.toIntOrNull() ?: 0,
        streamUrl = stream,
        downloadUrl = stream
    )
}
