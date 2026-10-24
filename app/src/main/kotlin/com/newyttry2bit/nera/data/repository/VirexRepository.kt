package com.newyttry2bit.nera.data.repository

import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSong

interface VirexRepository {
    suspend fun getCategoryMix(category: String): List<VirexSong>
    suspend fun searchSongs(query: String): List<VirexSong>
    suspend fun getDownloads(): List<VirexSong>
    suspend fun getPlaylists(): List<VirexPlaylist>
}
