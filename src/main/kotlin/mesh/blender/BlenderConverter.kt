package com.gigapi.mesh.blender

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.gigapi.mesh.BlenderRenderData
import com.gigapi.mesh.MaterialRenderData
import com.gigapi.mesh.RawMeshData
import com.gigapi.mesh.SubMeshRenderData

object BlenderConverter {

    fun convertToRenderModel(
        model: BlenderModel,
        appTexturesPath: String,
        assetManager: AssetManager
    ): BlenderRenderData {
        val subMeshesRenderData = model.subMeshes.map { subMesh ->
            val material = model.materials[subMesh.materialIndex]

            val rawMeshData = RawMeshData(subMesh.interleavedVertexData, subMesh.indices)

            val texture = material.diffuseMap?.let { texturePath ->
                assetManager.get<Texture>("$appTexturesPath/$texturePath")
            }

            val materialRenderData = MaterialRenderData(
                diffuseColor = material.diffuseColor,
                alpha = material.alpha,
                texture = texture
            )

            SubMeshRenderData(rawMeshData.createMeshData().mesh!!, materialRenderData)
        }

        return BlenderRenderData(subMeshesRenderData)
    }
}