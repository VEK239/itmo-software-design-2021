package com.vlasova.bridge.draw

import com.vlasova.bridge.figure.Circle
import com.vlasova.bridge.figure.Line
import com.vlasova.bridge.figure.Point
import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import javax.swing.JFrame

class AwtDrawing : DrawingApi {
    private val width = 800
    private val height = 800
    private val lines = mutableListOf<Line>()
    private val circles = mutableListOf<Circle>()

    override fun show() {
        val frame = JFrame("Graph")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(DrawLinesAndCircles(lines, circles))
        frame.pack()
        frame.size = Dimension(width, height)
        frame.isVisible = true
    }

    override fun drawCircle(circle: Circle) {
        circles.add(circle)
    }

    override fun drawLine(from: Point, to: Point) {
        lines.add(Line(from, to))
    }

    override fun getDrawingAreaWidth() = width
    override fun getDrawingAreaHeight() = height

    private class DrawLines(private val lines: List<Line>) : Canvas() {
        override fun paint(g: Graphics) {
            val g2 = g as Graphics2D
            g2.stroke = BasicStroke(2f)
            lines.forEach { (from, to) ->
                g2.draw(Line2D.Double(from.x, from.y, to.x, to.y))
            }
        }
    }

    private class DrawCircles(private val circles: List<Circle>) : Canvas() {
        override fun paint(g: Graphics) {
            val g2 = g as Graphics2D
            g2.stroke = BasicStroke(2f)
            circles.forEach { circle ->
                g2.fill(
                    Ellipse2D.Double(
                        circle.center.x - circle.radius,
                        circle.center.y - circle.radius,
                        circle.radius * 2,
                        circle.radius * 2
                    )
                )
            }
        }
    }

    private class DrawLinesAndCircles(lines: List<Line>, circles: List<Circle>) : Canvas() {
        private val drawLines = DrawLines(lines)
        private val drawCircles = DrawCircles(circles)
        override fun paint(g: Graphics) {
            drawLines.paint(g)
            drawCircles.paint(g)
        }
    }
}