package com.grandtour.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.grandtour.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class SoundId { SwipeLeft, SwipeRight, GameOver, Victory }

/**
 * Plays short SFX via SoundPool and an ambient loop via MediaPlayer.
 *
 * The actual audio files live in `res/raw/` and must be generated before
 * first build — see `res/raw/README.md`. Until they exist, [enabled] should
 * be left false and all play calls are no-ops.
 */
@Singleton
class AudioEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    var enabled: Boolean = false
        set(value) {
            field = value
            if (!value) stopMusic()
        }

    private val pool: SoundPool by lazy {
        SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    private val soundIds: Map<SoundId, Int> by lazy {
        // Wrapped in runCatching so absent assets don't crash; the engine
        // silently no-ops until the audio files are dropped into res/raw/.
        buildMap {
            tryLoad(SoundId.SwipeLeft, "sfx_swipe_left")
            tryLoad(SoundId.SwipeRight, "sfx_swipe_right")
            tryLoad(SoundId.GameOver, "sfx_game_over")
            tryLoad(SoundId.Victory, "sfx_victory")
        }
    }

    private fun MutableMap<SoundId, Int>.tryLoad(id: SoundId, resName: String) {
        val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (resId != 0) put(id, pool.load(context, resId, 1))
    }

    private var music: MediaPlayer? = null

    fun play(id: SoundId) {
        if (!enabled) return
        soundIds[id]?.let { pool.play(it, 0.9f, 0.9f, 1, 0, 1f) }
    }

    fun startMusic() {
        if (!enabled) return
        val resId = context.resources.getIdentifier("music_ambient", "raw", context.packageName)
        if (resId == 0) return
        stopMusic()
        music = MediaPlayer.create(context, resId)?.apply {
            isLooping = true
            setVolume(0.4f, 0.4f)
            start()
        }
    }

    fun stopMusic() {
        music?.runCatching {
            if (isPlaying) stop()
            release()
        }
        music = null
    }
}
