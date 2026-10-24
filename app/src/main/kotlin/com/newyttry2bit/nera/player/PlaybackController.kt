package com.newyttry2bit.nera.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.newyttry2bit.nera.data.model.LoopMode
import com.newyttry2bit.nera.data.model.VirexSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaybackController(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val exoPlayer = ExoPlayer.Builder(context).build()

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    init {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.value = _state.value.copy(isPlaying = isPlaying)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _state.value = _state.value.copy(currentIndex = exoPlayer.currentMediaItemIndex.coerceAtLeast(0))
                }
            }
        )

        scope.launch {
            while (isActive) {
                _state.value = _state.value.copy(
                    positionMs = exoPlayer.currentPosition.coerceAtLeast(0),
                    durationMs = exoPlayer.duration.takeIf { it > 0 } ?: 0L
                )
                delay(400)
            }
        }
    }

    fun setQueue(queue: List<VirexSong>, index: Int = 0, autoPlay: Boolean = true) {
        val mediaItems = queue.mapNotNull { song ->
            song.streamUrl?.let { url -> MediaItem.fromUri(url) }
        }
        if (mediaItems.isEmpty()) {
            _state.value = _state.value.copy(queue = queue, currentIndex = index.coerceAtLeast(0))
            return
        }

        exoPlayer.setMediaItems(mediaItems, index.coerceIn(0, mediaItems.lastIndex), 0L)
        exoPlayer.prepare()
        if (autoPlay) exoPlayer.play()

        _state.value = _state.value.copy(
            queue = queue,
            currentIndex = index.coerceIn(0, queue.lastIndex),
            isPlaying = autoPlay
        )
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs.coerceAtLeast(0))
    }

    fun playNext() = exoPlayer.seekToNextMediaItem()

    fun playPrevious() = exoPlayer.seekToPreviousMediaItem()

    fun setShuffle(enabled: Boolean) {
        exoPlayer.shuffleModeEnabled = enabled
        _state.value = _state.value.copy(shuffleEnabled = enabled)
    }

    fun setLoopMode(loopMode: LoopMode) {
        exoPlayer.repeatMode = when (loopMode) {
            LoopMode.Off -> Player.REPEAT_MODE_OFF
            LoopMode.One -> Player.REPEAT_MODE_ONE
            LoopMode.All -> Player.REPEAT_MODE_ALL
        }
        _state.value = _state.value.copy(loopMode = loopMode)
    }

    fun release() {
        exoPlayer.release()
    }
}

data class PlaybackState(
    val queue: List<VirexSong> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val loopMode: LoopMode = LoopMode.Off
) {
    val currentSong: VirexSong?
        get() = queue.getOrNull(currentIndex)
}
