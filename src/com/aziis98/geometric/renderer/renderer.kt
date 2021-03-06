package com.aziis98.geometric.renderer

import com.aziis98.deluengine.io.*
import com.aziis98.deluengine.maths.*
import com.aziis98.geometric.Geometric
import com.aziis98.geometric.event.*
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.ui.feature.input.*
import com.aziis98.geometric.ui.feature.render.*
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
const val TOOL_POINT_POSITION = "point-position"

const val TOOL_CENTROID        = "centroid"
const val TOOL_CENTROID_FIRST  = "centroid-start"
const val TOOL_CENTROID_APPEND = "centroid-append"

const val TOOL_LINE   = "line"
const val TOOL_LINE_A = "line-a"
const val TOOL_LINE_B = "line-b"

const val TOOL_LINE_PERPENDICULAR       = "line-perpendicular"
const val TOOL_LINE_PERPENDICULAR_LINE  = "line-perpendicular-line"
const val TOOL_LINE_PERPENDICULAR_POINT = "line-perpendicular-point"

const val TOOL_LINE_PARALLEL       = "line-parallel"
const val TOOL_LINE_PARALLEL_LINE  = "line-parallel-line"
const val TOOL_LINE_PARALLEL_POINT = "line-parallel-point"

const val TOOL_LINE_INTERSECTION   = "line-intersection"
const val TOOL_LINE_INTERSECTION_A = "line-intersection-a"
const val TOOL_LINE_INTERSECTION_B = "line-intersection-b"


// @formatter:on

class Renderer(override val owner: Box) : RenderFeature() {

    val statusTool by lazy { Geometric.ui.query("status-tool")!! }

    var state: String by Delegates.observable(TOOL_NONE) { property, oldValue, newValue ->
        statusTool.featureOfType<RenderTextFeature>()?.text = "TOOL: $newValue"
        statusTool.invalidate(2)
    }

    val handlers = HashMap<String, ITool>()
    var dragging: Point? = null

    val primitives = LinkedList<Primitive>()
    val camera = AffineTransform.getTranslateInstance(0.0, 0.0)

    init {
        // ADD DEP FEATURES
        val origin = Point(Vec2d(0, 0))
        primitives.add(origin)
//
//        val point2 = Point(Vec2d(100, 40))
//        primitives.add(point2)
//
//        val line1 = Line2Pt(point1, point2)
//        primitives += line1
//
//        val line2 = LinePerpendicular(line1, point2)
//        primitives += line2

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
            features += ClipRenderFeature(this)
        }

        Mouse.on<MouseReleased> {
            if (it.button == MouseEvent.BUTTON1 && dragging != null) {
                dragging = null
            }
        }

        GeometricEvents.on<ToolSelected> {
            val toolSeq = handlers[it.id] as ToolSequencer

            state = toolSeq.firstHandler.state
        }

        GeometricEvents.on<CanvasClicked> {
            val pt2d = (it.position - Vec2i(owner.width.toInt() / 2, owner.height.toInt() / 2)).toPoint2D()
            val screenToWorldPt = camera.inverseTransform(pt2d, null)

            state = (handlers[state] as ToolHander).handle(this, screenToWorldPt.toVec2i())
        }

        Keyboard.on<KeyReleased> {
            when (it.keyCode) {
                KeyEvent.VK_ENTER -> {
                    val handler = handlers[state]

                    if (handler is ToolHander) {
                        if (handler.needsManualTerminator) {
                            state = handler.manualTermination(this)
                        }
                    }
                }
                KeyEvent.VK_ESCAPE -> state = TOOL_NONE

                KeyEvent.VK_P -> println(primitives)
            }
        }
    }

    fun registerHandlers() {
        HandlePoint
        HandleLineIntersection
        HandleCentroid
        HandleLine
        HandleLinePerpendicular
        HandleLineParallel

        handlers.forEach { stateKey, toolValue -> println("$stateKey : $toolValue") }
        println()
    }

    internal val defStroke = BasicStroke(1.0F)
    internal val defColor = Color.BLACK

    override fun render(g: Graphics2D) {
        g.translate(owner.width.toInt() / 2, owner.height.toInt() / 2)
        g.transform(camera)

        primitives.forEach {
            g.stroke = defStroke
            g.color = defColor

            it.validate()
            it.highlighted = (handlers[state] as? ToolHander)?.highlight(this, it) ?: false
            it.render(this, g)
        }

        g.transform(camera.createInverse())
        g.translate(-owner.width.toInt() / 2, -owner.height.toInt() / 2)
    }

    fun registerTool(tool: ITool) {
        handlers.put(tool.state, tool)
    }

    val mouseWorldPos: Vec2d
        get() {
            val position = owner.toRelativeCoord(Mouse.position.toVec2i() - Vec2i(Geometric.insets.left, Geometric.insets.top))

            val pt2d = (position - Vec2i(owner.width.toInt() / 2, owner.height.toInt() / 2)).toPoint2D()
            val screenToWorldPt = camera.inverseTransform(pt2d, null)

            return screenToWorldPt.toVec2d()
        }

}



