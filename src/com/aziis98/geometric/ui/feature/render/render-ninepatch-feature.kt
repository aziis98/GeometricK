package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.*
import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

class RenderNinePatchFeature(override val owner: Box, val ninePatch: NinePatch, override var disabled: Boolean = false) : RenderFeature {
    override fun render(g: Graphics2D) {
        if (disabled) return

        g.drawNinePatchTexture(ninePatch, 0, 0, owner.width.toInt(), owner.height.toInt())
    }
}

fun Box.renderNinePatch(ninePatch: NinePatch) = RenderNinePatchFeature(this, ninePatch)