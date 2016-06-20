package com.aziis98.geometric.ui.feature.input

import com.aziis98.deluengine.io.*
import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.Feature


// Copyright 2016 Antonio De Lucreziis

class DragFeature(override val owner: Box,
                  val onDrag: Box.(button: Int, position: Vec2i, delta: Vec2i)->Unit) : Feature {
    init {
        Mouse.on<MouseDragged> {
            val relPosition = owner.toRelativeCoord(it.position.toVec2i() - Vec2i(Geometric.insets.left, Geometric.insets.top))

            if (owner.contains(relPosition))
                owner.onDrag(it.button, it.position.toVec2i(), it.delta.toVec2i())
        }
    }
}

fun Box.inputDrag(onDrag: Box.(button: Int, position: Vec2i, delta: Vec2i) -> Unit) = DragFeature(this, onDrag)