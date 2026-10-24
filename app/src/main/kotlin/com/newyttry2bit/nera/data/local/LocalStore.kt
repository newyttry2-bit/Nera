package com.newyttry2bit.nera.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.newyttry2bit.nera.data.model.VirexPlaylist
import com.newyttry2bit.nera.data.model.VirexSettings
import com.newyttry2bit.nera.data.model.VirexSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nera_store")

class LocalStore(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private object Keys {
        val PLAYLISTS = stringPreferencesKey("playlists")
        val DOWNLOADS = stringPreferencesKey("downloads")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
        val SETTINGS = stringPreferencesKey("settings")
    }

    val playlistsFlow: Flow<List<VirexPlaylist>> = context.dataStore.data.map { pref ->
        pref[Keys.PLAYLISTS]
            ?.let { raw -> json.decodeFromString(ListSerializer(VirexPlaylist.serializer()), raw) }
            ?: emptyList()
    }

    val downloadsFlow: Flow<List<VirexSong>> = context.dataStore.data.map { pref ->
        pref[Keys.DOWNLOADS]
            ?.let { raw -> json.decodeFromString(ListSerializer(VirexSong.serializer()), raw) }
            ?: emptyList()
    }

    val searchHistoryFlow: Flow<List<String>> = context.dataStore.data.map { pref ->
        pref[Keys.SEARCH_HISTORY]
            ?.let { raw -> json.decodeFromString(ListSerializer(String.serializer()), raw) }
            ?: emptyList()
    }

    val settingsFlow: Flow<VirexSettings> = context.dataStore.data.map { pref ->
        pref[Keys.SETTINGS]?.let { raw -> json.decodeFromString(VirexSettings.serializer(), raw) }
            ?: VirexSettings()
    }

    suspend fun setPlaylists(playlists: List<VirexPlaylist>) {
        context.dataStore.edit { pref ->
            pref[Keys.PLAYLISTS] = json.encodeToString(ListSerializer(VirexPlaylist.serializer()), playlists)
        }
    }

    suspend fun setDownloads(downloads: List<VirexSong>) {
        context.dataStore.edit { pref ->
            pref[Keys.DOWNLOADS] = json.encodeToString(ListSerializer(VirexSong.serializer()), downloads)
        }
    }

    suspend fun setSearchHistory(history: List<String>) {
        context.dataStore.edit { pref ->
            pref[Keys.SEARCH_HISTORY] = json.encodeToString(ListSerializer(String.serializer()), history)
        }
    }

    suspend fun setSettings(settings: VirexSettings) {
        context.dataStore.edit { pref ->
            pref[Keys.SETTINGS] = json.encodeToString(VirexSettings.serializer(), settings)
        }
    }
}
