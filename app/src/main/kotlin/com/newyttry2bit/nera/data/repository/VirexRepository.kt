package com.newyttry2bit.nera.data.repository

import com.newyttry2bit.nera.data.model.LyricsLine
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong
import kotlinx.coroutines.flow.Flow

interface VirexRepository {
    fun playlistsFlow(): Flow<List<VirexPlaylist>>
    fun downloadsFlow(): Flow<List<VirexSong>>
    fun searchHistoryFlow(): Flow<List<String>>
    fun settingsFlow(): Flow<VirexSettings>

    suspend fun getCategoryMix(category: String): List<VirexSong>
    suspend fun searchSongs(query: String): List<VirexSong>
    suspend fun getRecommendations(song: VirexSong): List<VirexSong>
    suspend fun getLyrics(song: VirexSong): List<LyricsLine>

    suspend fun createPlaylist(title: String)
    suspend fun importPlaylist(title: String, lines: List<String>)
    suspend fun addSongToPlaylist(playlistId: String, song: VirexSong)
    suspend fun removePlaylist(playlistId: String)

    suspend fun addToDownloads(song: VirexSong)
    suspend fun removeFromDownloads(songId: String)

    suspend fun addSearchHistory(query: String)
    suspend fun removeSearchHistory(query: String)
    suspend fun clearSearchHistory()

    suspend fun updateSettings(settings: VirexSettings)
}
