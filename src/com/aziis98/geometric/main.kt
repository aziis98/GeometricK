package com.aziis98.geometric

import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.RenderChildrenFeature
import com.aziis98.geometric.ui.feature.render.BorderFeature
import com.aziis98.geometric.util.tryRender
import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*
import kotlin.system.exitProcess

// Copyright 2016 Antonio De Lucreziis

fun main(args: Array<String>) { Geometric.start() }

object Geometric : Window() {

    val ui: WindowUI by lazy { WindowUI(this) }

    override fun initWindow() {
        size = size(1000, 800)
        title = "Geometric"
        resizeable = true
    }

    override fun init() {
        ui.apply {
            features += BorderFeature(this, Color.GREEN)
            features += RenderChildrenFeature(this)

            children += Box(this, left = 100.pk, right = 100.pk, top = 100.pk, bottom = 100.pk).apply {
                features += BorderFeature(this, Color.GREEN)
                features += RenderChildrenFeature(this)
            }
        }
    }

    override fun paint(g: Graphics2D) {
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        ui.tryRender(g)

        exitProcess(-1)
    }

    override fun update() {
        ui.updateLayout()
    }

}
