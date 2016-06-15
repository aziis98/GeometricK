package com.aziis98.geometric

import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.*
import com.aziis98.geometric.ui.feature.render.*
import com.aziis98.geometric.util.*
import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*

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
            features += renderBorder(Color.BLUE)
            features += renderChildren()

            val box1 = box(left = 100.pk, right = 100.pk, top = 0.pk, height = 100.pk).apply {
                features += renderBorder(Color.GREEN)
                features += renderChildren()
            } addTo children

            box(left = 200.pk, right = 200.pk, height = 75.pk).apply {
                features += layoutConstraint {
                    top = (box1.top + box1.height + 10).pk
                }
                features += renderBorder(Color.ORANGE)
                features += renderChildren()
                features += RenderNinePatchFeature(this, TextureLoader.ninePatch("/ui/nine-patch-test.png"))
            } addTo children
        }

        ui.updateLayout()

        println(ui.toString())
    }

    override fun paint(g: Graphics2D) {
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        ui.tryRender(g)
    }

    override fun update() {
        ui.updateLayout()
    }

}
