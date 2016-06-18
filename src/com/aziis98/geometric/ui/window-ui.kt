package com.aziis98.geometric.ui

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.util.*
import com.aziis98.geometric.window.Window
import java.awt.event.*

// Copyright 2016 Antonio De Lucreziis

class JFrameContainer(val window: Window) : IPackSized {
    override val width: PackedInt
        get() = (window.width - window.insets.left - window.insets.right).pk
    override val height: PackedInt
        get() = (window.height - window.insets.top - window.insets.bottom).pk

}

class WindowUI(window: Window) : Box(JFrameContainer(window), ZERO, ZERO, ZERO, ZERO, id = "window") {
    init {
        window.addMouseListener(object : MouseAdapter() {
            var previous = Vec2i(0, 0)
            var position = Vec2i(0, 0)

            override fun mouseMoved(e: MouseEvent) {
                previous = position
                position = Vec2i(e.x, e.y)


            }
        })
    }
}

