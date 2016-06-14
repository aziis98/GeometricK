package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.*


// Copyright 2016 Antonio De Lucreziis

class BorderFeature(owner: Box, var color: Color) : RenderFeature(owner) {

    override fun render(g: Graphics2D) {
        g.color = color
        g.drawRect(0, 0, owner.width.toInt(), owner.height.toInt())
    }

}