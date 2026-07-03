package com.gigapi.mesh

import com.badlogic.gdx.assets.AssetManager
import com.gigapi.effects.DisposableEffect
import com.gigapi.mesh.blender.BlenderConverter
import com.gigapi.mesh.blender.BlenderParser
import java.io.File

class ModelAssetManager(
    private val texturesPath: String,
    private val assetManager: AssetManager
): DisposableEffect {

    private val blenderParser = BlenderParser()
    private val blenderModelRenderMap = HashMap<Any, BlenderRenderData>()

    fun getRenderModel(key: Any): BlenderRenderData {
        val data = blenderModelRenderMap[key]!!
        return data
    }

    fun loadObj(key: Any, objFilePath: String, mltFilePath: String?) {
        val objFile = File(objFilePath)
        val mltFile = if(mltFilePath != null) File(mltFilePath) else null
        val blenderModel = blenderParser.parseObj(objFile, mltFile)
        blenderModelRenderMap[key] = BlenderConverter.convertToRenderModel(blenderModel, texturesPath, assetManager)
    }

    override fun dispose() {
        blenderModelRenderMap.forEach { (_, data) ->
            data.subMeshes.forEach { it.dispose() }
        }
        blenderModelRenderMap.clear()
    }
}