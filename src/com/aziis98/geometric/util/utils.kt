package com.aziis98.geometric.util

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

operator fun <E> MutableCollection<E>.plusAssign(element: E) {
    this.add(element)
}

fun Graphics2D.drawStringCentered(string: String, x: Int, y: Int) {
    val bounds = fontMetrics.getStringBounds(string, this)
    drawString(string, (x - bounds.width / 2).toInt(), y - (bounds.height / 2).toInt())
}

fun Box.tryRender(g: Graphics2D) {
    featuresOfType<RenderFeature>()
        .forEach { it.render(g) }
}