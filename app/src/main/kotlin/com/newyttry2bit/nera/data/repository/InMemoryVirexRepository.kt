package com.newyttry2bit.nera.data.repository

import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSong
import kotlinx.coroutines.delay

class InMemoryVirexRepository : VirexRepository {

    private val songs = listOf(
        VirexSong("1", "Satranga", "Arijit Singh", "https://via.placeholder.com/300"),
        VirexSong("2", "Lover", "Diljit Dosanjh", "https://via.placeholder.com/300"),
        VirexSong("3", "Blinding Lights", "The Weeknd", "https://via.placeholder.com/300"),
        VirexSong("4", "Obsessed", "Riar Saab", "https://via.placeholder.com/300"),
        VirexSong("5", "Husn", "Anuv Jain", "https://via.placeholder.com/300")
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

    override suspend fun getCategoryMix(category: String): List<VirexSong> {
        delay(150)
        return categories[category] ?: categories.getValue("Trending")
    }

    override suspend fun searchSongs(query: String): List<VirexSong> {
        delay(100)
        if (query.isBlank()) return emptyList()
        return songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getDownloads(): List<VirexSong> = songs.take(2)

    override suspend fun getPlaylists(): List<VirexPlaylist> {
        return listOf(
            VirexPlaylist(id = "pl-1", title = "Punjabi Charge", songs = songs.take(3)),
            VirexPlaylist(id = "pl-2", title = "Night Drive", songs = songs.takeLast(3))
        )
    }
}
