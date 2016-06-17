package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.util.tryRender
import java.awt.Graphics2D


// Copyright 2016 Antonio De Lucreziis

class RenderChildrenFeature(override val owner: Box, override var disabled: Boolean = false) : RenderFeature {
    override fun render(g: Graphics2D) {
        if (disabled) return

        owner.children.forEach {
            g.translate(it.left.value, it.top.value)
            it.tryRender(g)
            g.translate(-it.left.value, -it.top.value)
        }
    }
}

fun Box.renderChildren() = RenderChildrenFeature(this)