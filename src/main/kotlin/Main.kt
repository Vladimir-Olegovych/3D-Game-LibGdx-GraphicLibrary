package com.gigapi

import com.gigapi.mesh.blender.BlenderParser
import java.io.File

fun main() {
    val f = File("cube.obj")
    val b = BlenderParser()
    val model = b.parseObj(f)
    println("Загружено материалов: ${model.materials.size}")
    for ((idx, material) in model.materials.withIndex()) {
        println("  [$idx] ${material.name}: Цвет (${material.diffuseColor.joinToString()}), Текстура: ${material.diffuseMap ?: "отсутствует"}")
    }

    println("\nСгенерировано под-сеток: ${model.subMeshes.size}")
    for (subMesh in model.subMeshes) {
        val material = model.materials[subMesh.materialIndex]
        println("  Под-сетка с материалом '${material.name}': Вершин = ${subMesh.interleavedVertexData.size / 8}, Индексов = ${subMesh.indices.size}")
    }
}