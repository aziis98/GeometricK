package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.util.tryRender
import java.awt.Graphics2D


// Copyright 2016 Antonio De Lucreziis

class RenderChildrenFeature(owner: Box) : RenderFeature(owner) {
    override fun render(g: Graphics2D) {
        g.translate(owner.left, owner.right)

        owner.children.forEach {
            it.tryRender(g)
        }

        g.translate(-owner.left, -owner.right)
    }
}