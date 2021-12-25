package com.vlasova.bridge.graph

import com.vlasova.bridge.draw.DrawingApi
import com.vlasova.bridge.figure.Edge
import java.io.File

class ListGraph(
    drawingApi: DrawingApi,
    fileName: String
) : Graph(drawingApi) {
    private val vertexesNumber: Int
    private val edges: List<Edge>

    init {
        val lines = File(fileName).readLines().toMutableList()
        vertexesNumber = lines.first().toInt()
        lines.removeFirst()
        edges = lines.map { line ->
            val (x, y) = line.split(" ").map { it.toInt() }
            Edge(x, y)
        }
    }

    override fun drawGraph() {
        drawListGraph(vertexesNumber, edges)
    }
}