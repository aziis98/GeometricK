package com.aziis98.geometric.ui.feature.input

import com.aziis98.deluengine.io.*
import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.Feature

// Copyright 2016 Antonio De Lucreziis

class MouseClickFeature(override val owner: Box,
                        val onClick: Box.()->Unit) : Feature {
    init {
        Mouse.on<MouseClick> {
            if (owner.disabled) return@on

            val relPosition = owner.toRelativeCoord(Vec2i(it.x, it.y) - Vec2i(Geometric.insets.left, Geometric.insets.top))

            if (owner.contains(relPosition))
                owner.onClick()
        }
    }
}

fun Box.inputClick(onClick: Box.() -> Unit) = MouseClickFeature(this, onClick)