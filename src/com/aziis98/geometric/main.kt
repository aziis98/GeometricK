package com.aziis98.geometric

import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*

// Copyright 2016 Antonio De Lucreziis

fun main(args: Array<String>) { Geometric }

object Geometric : Window() {

    override fun initWindow() {
        size = size(1000, 900)
        title = "Geometric"
        resizeable = true
    }

    override fun paint(g: Graphics2D) {
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        g.color = Color.RED
        g.drawRect(300, 300, (100 + 50 * Math.sin(totalUpdates / 15.0)).toInt(), 100)
    }

    override fun update() {

    }

}
