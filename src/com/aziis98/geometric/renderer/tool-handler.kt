package com.aziis98.geometric.renderer

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric

// Copyright 2016 Antonio De Lucreziis

abstract class ToolHander(val state: String) {
    init {
        Geometric.renderer.registerHandler(this)
    }

    abstract fun handle(mousePos: Vec2i): String

    open val manualTerminator = false

    open fun manualTermination() { }

}

object HandlePoint : ToolHander(TOOL_POINT) {
    override fun handle(mousePos: Vec2i): String {
        Geometric.renderer.primitives.add(Point(mousePos.toVec2d()))
        return TOOL_NONE
    }
}