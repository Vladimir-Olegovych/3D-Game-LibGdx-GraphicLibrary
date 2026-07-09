package com.gigapi.mesh

import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.gigapi.effects.DisposableEffect

class MeshParams(
    val attributes : Array<VertexAttribute>,
    val stride: Int
)

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

    fun isEmpty(): Boolean = vertices.isEmpty()

    fun createMeshData(meshParams: MeshParams): MeshData {
        val mesh = Mesh(true, vertices.size / meshParams.stride, indices.size, *meshParams.attributes)
        mesh.setVertices(vertices)
        mesh.setIndices(indices)
        return MeshData(mesh = mesh)
    }
}
