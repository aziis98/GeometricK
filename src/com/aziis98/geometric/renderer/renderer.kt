package com.aziis98.geometric.renderer

import com.aziis98.deluengine.io.*
import com.aziis98.deluengine.maths.*
import com.aziis98.geometric.Geometric
import com.aziis98.geometric.event.*
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.ui.feature.input.*
import com.aziis98.geometric.ui.feature.render.RenderTextFeature
import com.aziis98.geometric.util.*
import java.awt.*
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.util.*
import kotlin.properties.Delegates

// Copyright 2016 Antonio De Lucreziis

// @formatter:off
const val TOOL_NONE           = "none"
const val TOOL_POINT          = "point"
const val TOOL_CENTROID_FIRST = "centroid-start"
const val TOOL_CENTROID       = "centroid"
const val TOOL_LINE_A         = "line-a"
const val TOOL_LINE_B         = "line-b"
// @formatter:on

class Renderer(override val owner: Box,
               override var disabled: Boolean = false) : RenderFeature {

    val statusTool by lazy { Geometric.ui.query("status-tool")!! }

    var state: String by Delegates.observable(TOOL_NONE) { property, oldValue, newValue ->
        statusTool.featureOfType<RenderTextFeature>().text = "TOOL: $newValue"
    }

    val handlers = HashMap<String, ToolHander>()
    var dragging: Point? = null

    val primitives = LinkedList<Primitive>()
    val camera = AffineTransform.getTranslateInstance(0.0, 0.0)

    init {
        // ADD DEP FEATURES
        val point1 = Point(Vec2d(0, 0))
        primitives.add(point1)

        val point2 = Point(Vec2d(100, 40))
        primitives.add(point2)

        val line1 = Line2Pt(point1, point2)
        primitives += line1

        val line2 = LinePerpendicular(line1, point2)
        primitives += line2

        owner.apply {
            features += inputClick { GeometricEvents.canvasClicked(toRelativeCoord(Mouse.position.toVec2i() - Vec2i(Geometric.insets.left, Geometric.insets.top))) }
            features += inputDrag { button, position, delta ->
                if (Mouse.button == MouseEvent.BUTTON2) {
                    camera.translate(delta.x.toDouble(), delta.y.toDouble())
                }

                val pt2d = (toRelativeCoord(Mouse.position.toVec2i() - Vec2i(owner.width.toInt() / 2, owner.height.toInt() / 2) - Vec2i(Geometric.insets.left, Geometric.insets.top))).toPoint2D()
                val screenToWorldPt = camera.inverseTransform(pt2d, null)

                if (Mouse.button == MouseEvent.BUTTON1) {
                    val nearestPoint = dragging ?: nearestPointOrNull(screenToWorldPt.toVec2d())
                    if (nearestPoint != null) {
                        dragging = nearestPoint

                        nearestPoint.position.x += delta.x
                        nearestPoint.position.y += delta.y

                        nearestPoint.invalidate()
                    }
                }
            }
        }

        Mouse.on<MouseReleased> {
            if (it.button == MouseEvent.BUTTON3 && dragging != null) {
                dragging = null
            }
        }

        GeometricEvents.on<ToolSelected> {
            state = it.id
        }

        GeometricEvents.on<CanvasClicked> {
            val pt2d = (it.position - Vec2i(owner.width.toInt() / 2, owner.height.toInt() / 2)).toPoint2D()
            val screenToWorldPt = camera.inverseTransform(pt2d, null)

            state = handlers[state]?.handle(this, screenToWorldPt.toVec2i()) ?: state
        }

        Keyboard.on<KeyReleased> {
            when (it.keyCode) {
                KeyEvent.VK_P -> println(primitives)
            }
        }
    }

    fun registerHandlers() {
        HandlePoint
        HandleLineA
        HandleLineB
    }

    override fun render(g: Graphics2D) {
        g.translate(owner.width.toInt() / 2, owner.height.toInt() / 2)
        g.transform(camera)

        primitives.forEach {
            it.validate()
            it.render(g)
        }

        g.transform(camera.createInverse())
        g.translate(-owner.width.toInt() / 2, -owner.height.toInt() / 2)
    }

    fun registerHandler(toolHander: ToolHander) {
        handlers.put(toolHander.state, toolHander)
    }

    fun nearestPointOrNull(mousePos: Vec2d) : Point? {
        return primitives
            .filterIsInstance<Point>()
            .filter { it.position distanceSquaredTo mousePos < 100.0 }
            .sortedBy { it.position distanceSquaredTo mousePos }
            .firstOrNull()
    }

}

abstract class Primitive() {
    val dependent: LinkedList<Primitive> = LinkedList()

    var dirty: Boolean = true
    var color: Color = Color.BLACK
    var highlighted: Boolean = false

    abstract fun render(g: Graphics2D)

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
    override fun render(g: Graphics2D) {
        if (highlighted) {
            g.color = Color.ORANGE
            g.drawCircle(x.toInt(), y.toInt(), 7)
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

    override fun render(g: Graphics2D) {
        if (highlighted) {
            g.color = Color.ORANGE
            g.stroke = BasicStroke(2.0F)
        }
        else {
            g.color = color
        }

        val renderer = Geometric.renderer
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