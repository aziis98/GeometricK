package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.*
import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

class RenderNinePatchFeature(owner: Box, val ninePatch: NinePatch) : RenderFeature(owner) {
    override fun render(g: Graphics2D) {
        g.drawNinePatchTexture(ninePatch, 0,0, owner.width.toInt(), owner.height.toInt())
    }
}