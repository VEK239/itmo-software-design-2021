package com.vlasova.bridge.draw

import com.vlasova.bridge.figure.Circle
import com.vlasova.bridge.figure.Point

interface DrawingApi {
    fun getDrawingAreaWidth(): Int
    fun getDrawingAreaHeight(): Int
    fun drawCircle(circle: Circle)
    fun drawLine(from: Point, to: Point)
    fun show()
}