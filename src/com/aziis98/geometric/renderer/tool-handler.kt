package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.*
import com.aziis98.geometric.Geometric
import java.util.*

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

    open val needsManualTerminator = false
    open fun manualTermination(renderer: Renderer): String { return TOOL_NONE }

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

object HandleLineIntersection : ToolSequencer(TOOL_LINE_INTERSECTION, listOf(_LineA, _LineB)) {
    var storedLine: Line? = null

    override val firstHandler = _LineA

    object _LineA : ToolHander(TOOL_LINE_INTERSECTION_A) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val line = renderer.nearestLineOrNull(mousePos.toVec2d())

            return if (line != null) {
                storedLine = line

                TOOL_LINE_INTERSECTION_B
            }
            else
                TOOL_LINE_INTERSECTION_A
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightLine(renderer, primitive)
    }

    object _LineB : ToolHander(TOOL_LINE_INTERSECTION_B) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val line = renderer.nearestLineOrNull(mousePos.toVec2d())

            return if (line != null && storedLine != line) {
                renderer.primitives += LineIntersection(storedLine!!, line)
                storedLine = null

                TOOL_NONE
            }
            else
                TOOL_LINE_INTERSECTION_B
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightLine(renderer, primitive)
            && storedLine != primitive
    }

}

object HandleCentroid : ToolSequencer(TOOL_CENTROID, listOf(_FirstPoint, _Point)) {
    var storedPoints = LinkedList<Point>()

    override val firstHandler = _FirstPoint

    object _FirstPoint : ToolHander(TOOL_CENTROID_FIRST) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            storedPoints = LinkedList()

            val candidatePoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (candidatePoint != null) {
                storedPoints.add(candidatePoint)
                return TOOL_CENTROID_APPEND
            }
            else {
                return TOOL_CENTROID_FIRST
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
    }

    object _Point : ToolHander(TOOL_CENTROID_APPEND) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val candidatePoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (candidatePoint != null) {
                storedPoints.add(candidatePoint)
            }

            return TOOL_CENTROID_APPEND
        }

        override val needsManualTerminator = true
        override fun manualTermination(renderer: Renderer): String {
            if (storedPoints.size < 2) return TOOL_NONE

            renderer.primitives += Centroid(storedPoints)

            return TOOL_NONE
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
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

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
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

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
            && primitive != HandleLine.storedLineA
    }

}

object HandleLineParallel : ToolSequencer(TOOL_LINE_PARALLEL, listOf(_Line, _Point)) {
    var storedLine: Line? = null

    override val firstHandler = _Line

    object _Line : ToolHander(TOOL_LINE_PARALLEL_LINE) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val nearestLine = renderer.primitives
                .filterIsInstance<Line>()
                .sortedBy { it.distanceToPoint(mousePos.toVec2d()) }
                .firstOrNull()

            if (nearestLine != null) {
                storedLine = nearestLine

                return TOOL_LINE_PARALLEL_POINT
            }
            else {
                return TOOL_LINE_PARALLEL_LINE
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightLine(renderer, primitive)
    }

    object _Point : ToolHander(TOOL_LINE_PARALLEL_POINT) {
        override fun handle(renderer: Renderer, mousePos: Vec2i): String {
            val candidatePoint = renderer.nearestPointOrNull(mousePos.toVec2d())

            if (candidatePoint != null) {
                renderer.primitives += LineParallel(storedLine!!, candidatePoint)
                storedLine = null

                return TOOL_NONE
            }
            else {
                return TOOL_LINE_PARALLEL_POINT
            }
        }

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
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

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightLine(renderer, primitive)
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

        override fun highlight(renderer: Renderer, primitive: Primitive) = highlightPoint(renderer, primitive)
    }

}

fun highlightLine(renderer: Renderer, primitive: Primitive) = primitive is Line
    && renderer.nearestLineOrNull(renderer.mouseWorldPos) == primitive

fun highlightPoint(renderer: Renderer, primitive: Primitive) = primitive is Point
    && renderer.nearestPointOrNull(renderer.mouseWorldPos) == primitive

fun Renderer.nearestPointOrNull(position: Vec2d) = primitives
    .filterIsInstance<Point>()
    .filter { it.position distanceSquaredTo position < 100.0 }
    .sortedBy { it.position distanceSquaredTo position }
    .firstOrNull()

fun Renderer.nearestLineOrNull(position: Vec2d) = primitives
    .filterIsInstance<Line>()
    .filter { it.distanceToPoint(position) < 10.0 }
    .sortedBy { it.distanceToPoint(position) }
    .firstOrNull()