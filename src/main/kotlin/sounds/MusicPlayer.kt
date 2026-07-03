package com.gigapi.sounds

import com.gigapi.effects.DisposableEffect

class MusicPlayer(
    private val musicAssetManager: MusicAssetManager
): DisposableEffect {
    private var currentMusicKey: Any? = null
    private var isPaused: Boolean = false
    private var volume: Float = 1.0f

    fun play(key: Any, looping: Boolean = true) {
        val music = musicAssetManager.get(key) ?: run {
            println("Music not found for key: $key")
            return
        }
        if (currentMusicKey != null && currentMusicKey != key) {
            pauseCurrent()
        }

        music.isLooping = looping
        music.volume = volume
        music.play()
        currentMusicKey = key
        isPaused = false
    }

    fun pause() {
        currentMusicKey?.let { key ->
            musicAssetManager.get(key)?.takeIf { it.isPlaying }?.pause()
            isPaused = true
        }
    }

    fun resume() {
        if (isPaused) {
            currentMusicKey?.let { key ->
                musicAssetManager.get(key)?.play()
                isPaused = false
            }
        }
    }

    fun stop() {
        currentMusicKey?.let { key ->
            musicAssetManager.get(key)?.stop()
            isPaused = false
        }
    }

    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        currentMusicKey?.let { key ->
            musicAssetManager.get(key)?.volume = this.volume
        }
    }

    fun isPlaying(): Boolean {
        return currentMusicKey?.let { key ->
            musicAssetManager.get(key)?.isPlaying ?: false
        } ?: false
    }

    fun getCurrentKey(): Any? = currentMusicKey

    private fun pauseCurrent() {
        currentMusicKey?.let { key ->
            musicAssetManager.get(key)?.takeIf { it.isPlaying }?.pause()
        }
    }

    override fun dispose() {
        stop()
    }
}