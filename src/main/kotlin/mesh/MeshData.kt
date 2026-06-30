package com.gigapi.mesh

import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.gigapi.core.effects.DisposableEffect

class MeshData(val mesh: Mesh? = null)

class BlenderRenderData(
    val subMeshes: List<SubMeshRenderData>
)

class SubMeshRenderData(
    val mesh: Mesh,
    val material: MaterialRenderData
): DisposableEffect {
    override fun dispose() {
        mesh.dispose()
        if (material.isTextureDisposable) {
            material.texture?.dispose()
        }
    }
}

class MaterialRenderData(
    val diffuseColor: FloatArray,
    val alpha: Float,
    val texture: GLTexture? = null,
    var isTextureDisposable: Boolean = false
)

class RawMeshData(
    val vertices: FloatArray,
    val indices: ShortArray,
) {

    companion object {
        const val PHYSICAL_STRIDE = 6
        const val STRIDE = 10
    }
    fun isEmpty(): Boolean = vertices.isEmpty()

    fun createPhysicalModel(): MeshData {
        val attributes = arrayOf(
            VertexAttribute(VertexAttributes.Usage.Position, 3, "a_Position"),
            VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_Normal")
        )
        val mesh = Mesh(true, vertices.size / PHYSICAL_STRIDE, indices.size, *attributes)
        mesh.setVertices(vertices)
        mesh.setIndices(indices)
        return MeshData(mesh = mesh)
    }

    fun createMeshData(): MeshData {
        val attributes = arrayOf(
            VertexAttribute(VertexAttributes.Usage.Position, 3, "a_Position"),
            VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_Normal"),
            VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_TexCoord"),
            VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_AO"),
            VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_Shadow")
        )
        val mesh = Mesh(true, vertices.size / STRIDE, indices.size, *attributes)
        mesh.setVertices(vertices)
        mesh.setIndices(indices)
        return MeshData(mesh = mesh)
    }
}
