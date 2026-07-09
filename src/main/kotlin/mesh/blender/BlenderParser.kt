package com.gigapi.mesh.blender

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.gigapi.mesh.MeshParams
import de.javagl.obj.ObjData
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjSplitting
import de.javagl.obj.ObjUtils
import java.io.File
import java.io.FileInputStream

class BlenderParser {
    
    companion object {
        val modelMeshParams = MeshParams(
            attributes = arrayOf(
                VertexAttribute(VertexAttributes.Usage.Position, 3, "a_Position"),
                VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_Normal"),
                VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_TexCoord"),
            ),
            stride = 8
        )
    }

    fun parseObj(objFile: File, mtlFile: File? = null): BlenderModel {
        val obj = FileInputStream(objFile).use {
            ObjUtils.convertToRenderable(ObjReader.read(it))
        }

        val materials = parseMaterials(mtlFile)
        val materialIndexMap = materials.withIndex().associate { it.value.name to it.index }

        var materialGroups = ObjSplitting.splitByMaterialGroups(obj)

        if (materialGroups.isEmpty()) {
            materialGroups = mapOf("default" to obj)
        }

        val subMeshes = mutableListOf<SubMesh>()

        for ((matName, groupObj) in materialGroups) {
            val matIndex = materialIndexMap[matName] ?: 0
            val renderableGroup = ObjUtils.convertToRenderable(groupObj)

            val vertices = ObjData.getVertices(renderableGroup)
            val normals = ObjData.getNormals(renderableGroup)
            val texCoords = ObjData.getTexCoords(renderableGroup, 2)
            val indices = ObjData.getFaceVertexIndices(renderableGroup)

            if (vertices.capacity() == 0) continue

            val vertexCount = vertices.capacity() / 3
            val interleaved = FloatArray(vertexCount * modelMeshParams.stride)

            for (i in 0 until vertexCount) {
                interleaved[i * modelMeshParams.stride] = vertices[i * 3]
                interleaved[i * modelMeshParams.stride + 1] = vertices[i * 3 + 1]
                interleaved[i * modelMeshParams.stride + 2] = vertices[i * 3 + 2]

                if (normals != null && normals.capacity() >= (i * 3 + 2)) {
                    interleaved[i * modelMeshParams.stride + 3] = normals[i * 3]
                    interleaved[i * modelMeshParams.stride + 4] = normals[i * 3 + 1]
                    interleaved[i * modelMeshParams.stride + 5] = normals[i * 3 + 2]
                } else {
                    interleaved[i * modelMeshParams.stride + 3] = 0f
                    interleaved[i * modelMeshParams.stride + 4] = 1f
                    interleaved[i * modelMeshParams.stride + 5] = 0f
                }

                if (texCoords != null && texCoords.capacity() >= (i * 2 + 1)) {
                    interleaved[i * modelMeshParams.stride + 6] = texCoords[i * 2]
                    interleaved[i * modelMeshParams.stride + 7] = 1f - texCoords[i * 2 + 1]
                } else {
                    interleaved[i * modelMeshParams.stride + 6] = 0f
                    interleaved[i * modelMeshParams.stride + 7] = 0f
                }
            }

            val shortIndices = ShortArray(indices.capacity())
            for (j in 0 until indices.capacity()) {
                shortIndices[j] = indices[j].toShort()
            }

            subMeshes.add(SubMesh(matIndex, interleaved, shortIndices))
        }

        return BlenderModel(materials, subMeshes)
    }

    private fun parseMaterials(mtlFile: File? = null): List<BlenderMaterial> {
        val blenderMaterials = mutableListOf<BlenderMaterial>().apply {
            add(BlenderMaterial("default"))
        }

        if (mtlFile == null || !mtlFile.exists()) return blenderMaterials

        var currentBlenderMaterial: BlenderMaterial? = null
        mtlFile.forEachLine { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("newmtl ") -> {
                    currentBlenderMaterial?.let { blenderMaterials.add(it) }
                    val name = trimmed.substringAfter("newmtl ").trim()
                    currentBlenderMaterial = BlenderMaterial(name)
                }
                trimmed.startsWith("Kd ") && currentBlenderMaterial != null -> {
                    val parts = trimmed.split("\\s+".toRegex())
                    if (parts.size >= 4) {
                        currentBlenderMaterial.diffuseColor = floatArrayOf(
                            parts[1].toFloat(),
                            parts[2].toFloat(),
                            parts[3].toFloat()
                        )
                    }
                }
                trimmed.startsWith("map_Kd ") && currentBlenderMaterial != null -> {
                    currentBlenderMaterial.diffuseMap = trimmed.substringAfter("map_Kd ").trim()
                }
                trimmed.startsWith("d ") && currentBlenderMaterial != null -> {
                    currentBlenderMaterial.alpha = trimmed.substringAfter("d ").trim().toFloat()
                }
            }
        }
        currentBlenderMaterial?.let { blenderMaterials.add(it) }

        return blenderMaterials
    }
}