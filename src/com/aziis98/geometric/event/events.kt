package com.aziis98.geometric.event

import com.aziis98.deluengine.event.*
import com.aziis98.deluengine.maths.Vec2i

// Copyright 2016 Antonio De Lucreziis

object GeometricEvents : EventEmitter<GeometricEvents>() {

    fun toolSelected(id: String) {
        emit(ToolSelected(id))
    }

    fun canvasClicked(position: Vec2i) {
        emit(CanvasClicked(position))
    }

}

class ToolSelected(val id: String) : Event<GeometricEvents>

class CanvasClicked(val position: Vec2i) : Event<GeometricEvents>