package com.gigapi.sounds

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.gigapi.core.effects.DisposableEffect

class MusicAssetManager: DisposableEffect {
    private val musicMap = HashMap<Any, Music>()

    fun get(key: Any): Music? = musicMap[key]

    fun load(key: Any, filePath: String) {
        val music = Gdx.audio.newMusic(Gdx.files.local(filePath))
        musicMap[key] = music
    }

    override fun dispose() {
        musicMap.values.forEach { it.dispose() }
        musicMap.clear()
    }
}