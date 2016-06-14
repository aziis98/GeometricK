package com.aziis98.geometric.window

import java.awt.Canvas
import javax.swing.*

// Copyright 2016 Antonio De Lucreziis

abstract class Window : Canvas() {

    val jframe: JFrame = JFrame().apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    // Abstract Methods

    abstract fun initWindow()

    // Binding properties

    var title: String
        get() = jframe.title
        set(value) { jframe.title = value }

    var resizeable: Boolean
        get() = jframe.isResizable
        set(value) { jframe.isResizable = value }

    var size: WindowSize
        get() = WindowSize(jframe.width, jframe.height)
        set(value) {
            jframe.setSize(
                if (value.width  < 0) width  else value.width,
                if (value.height < 0) height else value.height
            )
        }

    override fun getWidth() = jframe.width

    override fun getHeight() = jframe.height

    fun setWidth(value: Int) {
        jframe.setSize(value, height)
    }

    fun setHeight(value: Int) {
        jframe.setSize(width, value)
    }

}

data class WindowSize(val width: Int = -1, val height: Int = -1)

fun size(width: Int, height: Int) = WindowSize(width, height)
