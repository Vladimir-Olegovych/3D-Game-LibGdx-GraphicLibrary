package com.gigapi.mesh

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.gigapi.effects.DisposableEffect
import com.gigapi.mesh.blender.BlenderConverter
import com.gigapi.mesh.blender.BlenderModel
import com.gigapi.mesh.blender.BlenderParser

class ModelAssetManager(
    private val texturesPath: String,
    private val assetManager: AssetManager
): DisposableEffect {

    private val blenderParser = BlenderParser()
    private val blenderModelRenderMap = HashMap<Any, BlenderModel>()

    fun getRenderModel(key: Any): BlenderRenderData {
        val blenderModel = blenderModelRenderMap[key]!!
        val renderData = BlenderConverter.convertToRenderModel(blenderModel, texturesPath, assetManager)
        return renderData
    }

    fun loadObj(key: Any, objFilePath: String, mltFilePath: String?) {
        val objFile = Gdx.files.internal(objFilePath)
        val mltFile = if (mltFilePath != null) Gdx.files.internal(mltFilePath) else null
        val blenderModel = blenderParser.parseObj(objFile, mltFile)
        blenderModelRenderMap[key] = blenderModel
    }

    override fun dispose() {
        blenderModelRenderMap.clear()
    }
}
