package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric

// Copyright 2016 Antonio De Lucreziis

abstract class ITool(val state: String)

abstract class ToolSequencer(state: String, @Suppress("UNUSED_PARAMETER") descriptor: List<ToolHander>) : ITool(state) {
    init {
        Geometric.renderer.registerTool(this)
    }

    abstract val firstHandler: ToolHander
}

abstract class ToolHander(state: String) : ITool(state) {
    init {
        Geometric.renderer.registerTool(this)
    }

    abstract fun handle(renderer: Renderer, mousePos: Vec2i): String

    abstract fun highlight(renderer: Renderer, primitive: Primitive): Boolean

    open val manualTerminator = false

    open fun manualTermination() { }

}

object HandlePoint : ToolSequencer(TOOL_POINT, listOf(_Position)) {
    override val firstHandler = _Position

    object _Position : ToolHander(TOOL_POINT_POSITION) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            renderer.primitives.add(Point(mousePos.toVec2d()))
            return TOOL_NONE
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = false
    }
}

object HandleLine : ToolSequencer(TOOL_LINE, listOf(_A, _B)) {
    var storedLineA: Point? = null

    override val firstHandler = _A

    object _A : ToolHander(TOOL_LINE_A) {

        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val optionalNearestPoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (optionalNearestPoint != null) {
                HandleLine.storedLineA = optionalNearestPoint

                return TOOL_LINE_B
            }
            else {
                return TOOL_LINE_A
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = primitive is Point
            && renderer.nearestPointOrNull(renderer.mouseWorldPos) == primitive
    }

    object _B : ToolHander(TOOL_LINE_B) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {

            val optionalNearestPoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (optionalNearestPoint != null && optionalNearestPoint != HandleLine.storedLineA) {
                renderer.primitives += Line2Pt(HandleLine.storedLineA!!, optionalNearestPoint)

                HandleLine.storedLineA = null

                return TOOL_NONE
            }
            else {
                return TOOL_LINE_B
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = primitive is Point
            && renderer.nearestPointOrNull(renderer.mouseWorldPos) == primitive
            && primitive != HandleLine.storedLineA
    }

}

object HandleLinePerpendicular : ToolSequencer(TOOL_LINE_PERPENDICULAR, listOf(_Line, _Point)) {
    var storedLine: Line? = null

    override val firstHandler = _Line

    object _Line : ToolHander(TOOL_LINE_PERPENDICULAR_LINE) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val nearestLine = renderer.primitives
                .filterIsInstance<Line>()
                .sortedBy { it.distanceToPoint(mousePos.toVec2d()) }
                .firstOrNull()

            if (nearestLine != null) {
                storedLine = nearestLine

                return TOOL_LINE_PERPENDICULAR_POINT
            }
            else {
                return TOOL_LINE_PERPENDICULAR_LINE
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = primitive is Line
            && primitive.distanceToPoint(renderer.mouseWorldPos) < 10.0
    }

    object _Point : ToolHander(TOOL_LINE_PERPENDICULAR_POINT) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val candidatePoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (candidatePoint != null) {
                renderer.primitives += LinePerpendicular(storedLine!!, candidatePoint)
                storedLine = null

                return TOOL_NONE
            }
            else {
                return TOOL_LINE_PERPENDICULAR_POINT
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = primitive is Line
    }

}
