package com.aziis98.geometric

import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.RenderChildrenFeature
import com.aziis98.geometric.ui.feature.render.BorderFeature
import com.aziis98.geometric.util.tryRender
import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*
import kotlin.properties.Delegates.notNull

// Copyright 2016 Antonio De Lucreziis

fun main(args: Array<String>) { Geometric }

object Geometric : Window() {

    var ui: WindowUI by notNull()

    override fun initWindow() {
        size = size(1000, 900)
        title = "Geometric"
        resizeable = true

        ui = WindowUI(this)

        ui.apply {
            children += Box(this, left = 100, right = 100, top = 100, bottom = 100).apply {
                features += BorderFeature(this, Color.GREEN)
                features += RenderChildrenFeature(this)
            }
        }
    }

    override fun paint(g: Graphics2D) {
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        g.color = Color.RED
        g.drawRect(300, 300, (100 + 50 * Math.sin(totalUpdates / 15.0)).toInt(), 100)

        ui.tryRender(g)
    }

    override fun update() {

    }

}
