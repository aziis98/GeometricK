package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.*
import com.aziis98.geometric.util.*
import java.awt.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

val COLOR_PRIMITIVE_HIGHLIGHT = Color(0xff6200)

abstract class Primitive() {
    val dependent: LinkedList<Primitive> = LinkedList()

    var dirty: Boolean = true
    var color: Color = Color.BLACK
    var highlighted: Boolean = false

    abstract fun render(renderer: Renderer, g: Graphics2D)

    open fun recompute() { }

    /**
     * Destroys all the primitives that depends on this primitive
     */
    open fun destroy() {
        dependent.forEach { it.destroy() }
    }

    fun validate() {
        if (dirty) {
            recompute()
            dirty = false
        }
    }

    fun invalidate() {
        dirty = true
        dependent.forEach { it.invalidate() }
    }
}

class Point(val position: Vec2d) : Primitive() {
    override fun render(renderer: Renderer, g: Graphics2D) {
        if (highlighted) {
            g.color = COLOR_PRIMITIVE_HIGHLIGHT
            g.drawCircle(x.toInt(), y.toInt(), 8)
        }

        g.color = color
        g.drawCircle(position.x.toInt(), position.y.toInt(), 5)
        g.fillCircle(position.x.toInt(), position.y.toInt(), 3)
    }

    val x: Double
        get() = position.x

    val y: Double
        get() = position.y

    override fun toString(): String {
        return "Point#$nuid($position)"
    }
}

open class Line(var a: Double, var b: Double, var c: Double) : Primitive() {
    internal var p1 = Vec2i(0, 0)
    internal var p2 = Vec2i(0, 0)

    override fun render(renderer: Renderer, g: Graphics2D) {
        if (highlighted) {
            g.color = COLOR_PRIMITIVE_HIGHLIGHT
            g.stroke = BasicStroke(2.0F)
        }
        else {
            g.stroke = BasicStroke(1.0F)
            g.color = color
        }

        val ty = -renderer.camera.translateY - renderer.owner.height.toInt() / 2

        val y = c / b

        val x1 = (c - b * (ty)) / a
        val x2 = (c - b * (ty + renderer.owner.height.toInt())) / a

        if (a != 0.0) {
            p1.x = x1.toInt()
            p1.y = ty.toInt()

            p2.x = x2.toInt()
            p2.y = (ty + renderer.owner.height.toInt()).toInt()
        }
        else {
            p1.x = -renderer.camera.translateX.toInt() - renderer.owner.width.toInt() / 2
            p1.y = y.toInt()

            p2.x = -renderer.camera.translateX.toInt() + renderer.owner.width.toInt() / 2
            p2.y = y.toInt()
        }

        g.drawLine(p1.x, p1.y, p2.x, p2.y)
    }

    fun distanceToPoint(position: Vec2d): Double {
        return Math.abs(a * position.x + b * position.y + c) / Math.sqrt(a * a + b * b)
    }

    override fun toString(): String {
        return "Line#$nuid( ($a)x + ($b)y + $c = 0 )"
    }
}

class Line2Pt(val pointA: Point, val pointB: Point) : Line(pointB.y - pointA.y, pointA.x - pointB.x, pointA.x * pointB.y - pointA.y * pointB.x) {

    init {
        pointA.dependent += this
        pointB.dependent += this
    }

    override fun recompute() {
        a = pointB.y - pointA.y
        b = pointA.x - pointB.x
        c = pointA.x * pointB.y - pointA.y * pointB.x
    }

    override fun destroy() {
        super.destroy()

        pointA.dependent -= this
        pointB.dependent -= this
    }

    override fun toString(): String {
        return "Line#$nuid(#${pointA.nuid} , #${pointB.nuid})"
    }
}

class LinePerpendicular(val line: Line, val point: Point) : Line(line.b, -line.a, point.x * line.b - point.y * line.a) {
    init {
        line.dependent += this
        point.dependent += this
    }

    override fun recompute() {
        a = line.b
        b = -line.a
        c = point.x * line.b - point.y * line.a
    }

    override fun destroy() {
        super.destroy()

        line.dependent -= this
        point.dependent -= this
    }

    override fun toString(): String {
        return "LinePerpendicular#$nuid(#${line.nuid} , #${point.nuid})"
    }
}

val Primitive.nuid: Int
    get() = hashCode() % 100