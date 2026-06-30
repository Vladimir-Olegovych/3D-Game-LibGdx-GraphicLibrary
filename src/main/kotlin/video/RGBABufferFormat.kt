package com.gigapi.video

import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat

class RGBABufferFormat(
    sourceWidth: Int,
    sourceHeight: Int
) : BufferFormat(
    "RGBA",                             // chroma
    sourceWidth,                        // width
    sourceHeight,                       // height
    intArrayOf(sourceWidth * 4),        // pitches
    intArrayOf(sourceHeight)            // lines
)