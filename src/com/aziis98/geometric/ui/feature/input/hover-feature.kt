package com.aziis98.geometric.ui.feature.input

import com.aziis98.deluengine.io.*
import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.Feature

// Copyright 2016 Antonio De Lucreziis

class MouseHoverFeature(override val owner: Box,
                        val onEnter: Box.() -> Unit,
                        val onExit:  Box.() -> Unit) : Feature {

    init {
        Mouse.on<MouseMoved> {
            val relPosition = owner.toRelativeCoord(it.position.toVec2i() - Vec2i(Geometric.insets.left, Geometric.insets.top))
            val delta = it.delta.toVec2i()

            val posInside = owner.contains(relPosition)
            val prevInside = owner.contains(relPosition - delta)

            if (posInside && !prevInside) {
                owner.onEnter()
            }
            if (!posInside && prevInside) {
                owner.onExit()
            }

//            println("${owner.id} -> $relPosition")
        }
    }
}

fun Box.inputHover(onEnter: Box.() -> Unit, onExit: Box.() -> Unit) = MouseHoverFeature(this, onEnter, onExit)