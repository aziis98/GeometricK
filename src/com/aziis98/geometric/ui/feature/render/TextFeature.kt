package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.util.drawStringCentered
import java.awt.Graphics2D


// Copyright 2016 Antonio De Lucreziis

class TextFeature(owner: Box, var text: String) : RenderFeature(owner) {

    override fun render(g: Graphics2D) {
        g.drawStringCentered(text, owner.width / 2, owner.height / 2)
    }

}