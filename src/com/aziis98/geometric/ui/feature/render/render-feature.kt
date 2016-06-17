package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

class RenderizeFeature(override val owner: Box,
                       val renderAction: Box.(Graphics2D) -> Unit,
                       override var disabled: Boolean = false) : RenderFeature {
    override fun render(g: Graphics2D) {
        if (disabled) return

        owner.renderAction(g)
    }
}

fun Box.renderize(renderAction: Box.(Graphics2D) -> Unit) = RenderizeFeature(this, renderAction)