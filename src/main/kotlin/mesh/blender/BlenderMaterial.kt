package com.gigapi.mesh.blender

class BlenderMaterial(
    val name: String,
    var diffuseColor: FloatArray = floatArrayOf(0.8f, 0.8f, 0.8f),
    var alpha: Float = 1f,
    var diffuseMap: String? = null
)
