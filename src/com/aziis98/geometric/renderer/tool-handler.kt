package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric

// Copyright 2016 Antonio De Lucreziis

abstract class ToolHander(val state: String) {
    init {
        Geometric.renderer.registerHandler(this)
    }

    abstract fun handle(renderer: Renderer, mousePos: Vec2i): String

    open val manualTerminator = false

    open fun manualTermination() { }

}

object HandlePoint : ToolHander(TOOL_POINT) {
    override fun handle(renderer: Renderer, mousePos: Vec2i): String {
        renderer.primitives.add(Point(mousePos.toVec2d()))
        return TOOL_NONE
    }
}

object HandleLineA : ToolHander(TOOL_LINE_A) {
    var storedLineA: Point? = null

    override fun handle(renderer: Renderer, mousePos: Vec2i): String {
        val optionalNearestPoint = renderer.primitives
            .filterIsInstance<Point>()
            .filter { it.position distanceSquaredTo mousePos.toVec2d() < 25.0 }
            .sortedBy { it.position distanceSquaredTo mousePos.toVec2d() }
            .firstOrNull()

        if (optionalNearestPoint != null) {
            storedLineA = optionalNearestPoint

            return TOOL_LINE_B
        }
        else {
            return TOOL_LINE_A
        }
    }
}

object HandleLineB : ToolHander(TOOL_LINE_B) {
    override fun handle(renderer: Renderer, mousePos: Vec2i): String {

        val optionalNearestPoint = renderer.primitives
            .filterIsInstance<Point>()
            .filter { it.position distanceSquaredTo mousePos.toVec2d() < 25.0 }
            .sortedBy { it.position distanceSquaredTo mousePos.toVec2d() }
            .firstOrNull()

        if (optionalNearestPoint != null && optionalNearestPoint != HandleLineA.storedLineA) {
            renderer.primitives += Line2Pt(HandleLineA.storedLineA!!, optionalNearestPoint)

            HandleLineA.storedLineA = null

            return TOOL_NONE
        }
        else {
            return TOOL_LINE_B
        }
    }
}