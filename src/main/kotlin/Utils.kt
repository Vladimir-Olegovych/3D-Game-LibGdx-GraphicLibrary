package com.gigapi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

fun SpriteBatch.fillDraw(texture: Texture, camera: Camera) {
    val corner = camera.getWorldCorner()
    val ratio = texture.width / texture.height.toFloat()
    val cameraRatio = camera.viewportWidth / camera.viewportHeight

    if (ratio > cameraRatio) {
        val width = camera.viewportHeight * ratio
        val positionX = corner.x + (camera.viewportWidth - width) / 2f
        draw(texture, positionX, corner.y, width, camera.viewportHeight)
    } else {
        val height = camera.viewportWidth / ratio
        val positionY = corner.y + (camera.viewportHeight - height) / 2f
        draw(texture, corner.x, positionY, camera.viewportWidth, height)
    }
}

fun SpriteBatch.fillDraw(textureRegion: TextureRegion, camera: Camera) {
    val corner = camera.getWorldCorner()
    val ratio = textureRegion.regionWidth / textureRegion.regionHeight.toFloat()
    if (ratio > camera.viewportWidth / camera.viewportHeight) {
        val width = camera.viewportHeight * ratio
        val positionX = corner.x + (camera.viewportWidth - width) / 2f
        draw(textureRegion, positionX, corner.y, width, camera.viewportHeight)
    } else {
        val height = camera.viewportWidth / ratio
        val positionY = corner.y + (camera.viewportHeight - height) / 2f
        draw(textureRegion, corner.x, positionY, camera.viewportWidth, height)
    }
}

fun Camera.getWorldCorner(): Vector2 = Vector2(position.x - viewportWidth / 2f, position.y - viewportHeight / 2f)

inline fun <T : Actor> T.setOnClickListener(crossinline action: () -> Unit): T = apply {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            super.clicked(event, x, y)
            Gdx.app.postRunnable { action() }
        }
    })
}

inline fun <T : Actor> T.setOnTouchDraggedListener(crossinline action: (x: Float, y: Float) -> Unit): T = apply {
    addListener(object : InputListener() {
        override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
            super.touchDragged(event, x, y, pointer)
            Gdx.app.postRunnable { action(x, y) }
        }
    })
}