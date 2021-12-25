package com.vlasova.bridge

import com.vlasova.bridge.draw.AwtDrawing
import com.vlasova.bridge.draw.FxDrawing
import com.vlasova.bridge.graph.ListGraph
import com.vlasova.bridge.graph.MatrixGraph
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val drawer = when (args[0]) {
        "awt" -> AwtDrawing()
        "fx" -> FxDrawing()
        else -> throw IllegalArgumentException("Illegal framework, use awt/fx")
    }

    val graph = when (args[1]) {
        "list" -> ListGraph(drawer, "src/main/resources/list.txt")
        "matrix" -> MatrixGraph(drawer, "src/main/resources/matrix.txt")
        else -> throw IllegalArgumentException("Invalid format of file")
    }

    graph.drawGraph()
}



