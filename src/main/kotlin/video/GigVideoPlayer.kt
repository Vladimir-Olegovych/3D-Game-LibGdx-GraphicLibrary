package com.gigapi.video

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import java.nio.ByteBuffer

class GigVideoPlayer(val repeatable: Boolean) : Disposable {

    private val factory: MediaPlayerFactory = MediaPlayerFactory()
    private val mediaPlayer: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()

    private var texture: Texture? = null
    private var pixmap: Pixmap? = null
    private var buffer: ByteBuffer? = null

    private var volume = 1f

    @Volatile
    private var hasNewFrame = false
    private val lock = Any()

    init { setupVideoSurface() }

    private fun setupVideoSurface() {
        val bufferFormatCallback = object : BufferFormatCallback {
            override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
                synchronized(lock) {
                    val oldPixmap = pixmap
                    val oldTexture = texture
                    Gdx.app.postRunnable {
                        oldTexture?.dispose()
                        oldPixmap?.dispose()
                    }

                    pixmap = Pixmap(sourceWidth, sourceHeight, Pixmap.Format.RGBA8888)
                    buffer = pixmap?.pixels
                    texture = null
                }

                return RGBABufferFormat(sourceWidth, sourceHeight)
            }

            override fun allocatedBuffers(buffers: Array<out ByteBuffer>?) {}
        }

        val renderCallback = object : RenderCallback {
            override fun display(
                mediaPlayer: MediaPlayer?,
                nativeBuffers: Array<out ByteBuffer?>?,
                bufferFormat: BufferFormat?
            ) {
                if (nativeBuffers.isNullOrEmpty()) return
                val sourceBuf = nativeBuffers[0] ?: return

                synchronized(lock) {
                    val currentBuffer = buffer ?: return
                    try {
                        sourceBuf.clear()
                        currentBuffer.clear()

                        val bytesToCopy = minOf(sourceBuf.remaining(), currentBuffer.remaining())
                        sourceBuf.limit(bytesToCopy)

                        currentBuffer.put(sourceBuf)

                        currentBuffer.flip()
                        sourceBuf.clear()

                        hasNewFrame = true
                    } catch (e: Exception) {
                        Gdx.app.log("GigVideoPlayer", "Error copying frame: ${e.message}")
                    }
                }
            }
        }

        val videoSurface = CallbackVideoSurface(
            bufferFormatCallback,
            renderCallback,
            true,
            VideoSurfaceAdapters.getVideoSurfaceAdapter()
        )

        mediaPlayer.controls().repeat = repeatable

        mediaPlayer.videoSurface().set(videoSurface)
    }

    fun getTexture(): Texture? {
        synchronized(lock) {
            val currentPixmap = pixmap ?: return null

            if (texture == null) {
                texture = Texture(currentPixmap)
                texture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            }

            if (hasNewFrame) {
                texture?.draw(currentPixmap, 0, 0)
                hasNewFrame = false
            }
        }
        return texture
    }


    fun load(fileHandle: FileHandle) {
        val path = fileHandle.file().absolutePath
        mediaPlayer.media().prepare(path)
        setVolume(volume)
    }

    fun play() {
        mediaPlayer.controls().play()
    }

    fun pause() {
        mediaPlayer.controls().pause()
    }

    fun setVolume(vol: Float): Boolean {
        this.volume = vol.coerceIn(0f, 1f)
        return mediaPlayer.audio().setVolume((this.volume * 200).toInt())
    }


    override fun dispose() {
        mediaPlayer.controls().stop()
        mediaPlayer.release()
        factory.release()

        Gdx.app.postRunnable {
            texture?.dispose()
            pixmap?.dispose()
        }
    }
}
