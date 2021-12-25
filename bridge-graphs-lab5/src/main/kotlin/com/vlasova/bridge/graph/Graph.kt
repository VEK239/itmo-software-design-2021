package com.vlasova.bridge.graph

import com.vlasova.bridge.figure.Circle
import com.vlasova.bridge.draw.DrawingApi
import com.vlasova.bridge.figure.Edge
import com.vlasova.bridge.figure.Point
import java.lang.Integer.min
import kotlin.math.cos
import kotlin.math.sin


abstract class Graph(private val drawingApi: DrawingApi) {
    abstract fun drawGraph()

    protected fun drawListGraph(vertexesNumber: Int, edges: List<Edge>) {
        val points = pointsFromVertexesNumber(vertexesNumber)
        drawVertexes(points)
        drawEdges(points, edges)
        drawingApi.show()
    }

    private fun pointsFromVertexesNumber(vertexesNumber: Int): List<Point> {
        val a = drawingApi.getDrawingAreaWidth() / 2
        val b = drawingApi.getDrawingAreaHeight() / 2
        val r = min(a, b) * 0.6
        val points = mutableListOf<Point>()
        for (i in 0..vertexesNumber) {
            val t: Double = 2 * Math.PI * i / vertexesNumber
            val x = a + r * cos(t)
            val y = b + r * sin(t)
            points.add(Point(x, y))
        }
        return points
    }

    private fun drawVertexes(points: List<Point>) {
        val vertexRadius = 10.0
        points.forEach { point ->
            drawingApi.drawCircle(Circle(point, vertexRadius))
        }
    }

    private fun drawEdges(points: List<Point>, edges: List<Edge>) {
        edges.forEach { edge ->
            drawingApi.drawLine(points[edge.from], points[edge.to])
        }
    }
}