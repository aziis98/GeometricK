package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.Vec2d
import com.aziis98.geometric.event.*
import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.util.*
import java.awt.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

// @formatter:off
const val TOOL_NONE           = "tool-none"
const val TOOL_POINT          = "tool-point"
const val TOOL_CENTROID_FIRST = "tool-centroid-start"
const val TOOL_CENTROID       = "tool-centroid"
const val TOOL_LINE_A         = "tool-line-1"
const val TOOL_LINE_B         = "tool-line-2"
// @formatter:on

class Renderer(override val owner: Box,
               override var disabled: Boolean = false) : RenderFeature {

    var state: String = ""

    val handlers = HashMap<String, ToolHander>()

    val primitives = LinkedList<Primitive>()

    init {
        GeometricEvents.on<ToolSelected> {
            state = it.id
        }

        GeometricEvents.on<CanvasClicked> {
            state = handlers[state]?.handle(it.position) ?: state
        }
    }

    fun registerHandlers() {
        HandlePoint
    }

    override fun render(g: Graphics2D) {
        primitives.forEach { it.render(g) }
    }

    fun registerHandler(toolHander: ToolHander) {
        handlers.put(toolHander.state, toolHander)
    }

}

abstract class Primitive {
    abstract fun render(g: Graphics2D)
}

class Point(val position: Vec2d) : Primitive() {
    override fun render(g: Graphics2D) {
        g.color = Color.BLACK
        g.drawCircle(position.x.toInt(), position.y.toInt(), 5)
        g.fillCircle(position.x.toInt(), position.y.toInt(), 3)
    }
}