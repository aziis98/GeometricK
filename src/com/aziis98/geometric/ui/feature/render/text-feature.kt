package com.aziis98.geometric.ui.feature.render

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.*
import com.aziis98.geometric.util.drawStringCentered
import java.awt.*
import java.awt.geom.Rectangle2D


// Copyright 2016 Antonio De Lucreziis

val DEFAULT_FONT = Font("Segoe UI", Font.PLAIN, 13)

class RenderTextFeature(override val owner: Box,
                        var text: String,
                        var color: Color,
                        var font: Font = DEFAULT_FONT,
                        var offset: Vec2i = Vec2i(0, 0),
                        val constraint: (Int, Int) -> Unit = { w, h -> },
                        override var disabled: Boolean = false) : RenderFeature, ConstraintFeature {

    internal var bounds: Rectangle2D? = null

    override fun render(g: Graphics2D) {
        if (disabled) return

        g.color = color
        g.font = font
        bounds = g.drawStringCentered(text, owner.width.toInt() / 2 + offset.x, owner.height.toInt() / 2 + offset.y)
    }

    override fun updateConstraint() {
        if (bounds != null) constraint(bounds!!.width.toInt(), bounds!!.height.toInt())
    }

}

fun Box.renderText(text: String, color: Color, font: Font = DEFAULT_FONT, offset: Vec2i = Vec2i(0, 0), constraint: (Int, Int) -> Unit = { w, h -> }) =
    RenderTextFeature(this, text, color, font, offset, constraint)