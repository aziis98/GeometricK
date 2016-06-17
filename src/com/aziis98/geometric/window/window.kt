package com.aziis98.geometric.window

import com.aziis98.deluengine.io.*
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import javax.swing.*
import kotlin.concurrent.thread

// Copyright 2016 Antonio De Lucreziis

abstract class Window : JFrame() {

    init {
        initWindow()

        Mouse.registerToComponent(this)
        Keyboard.registerToComponent(this)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                renderInternal()
            }
        })

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        background = Color.BLACK
        contentPane.background = Color(0, 0, 0, 0)

        System.setProperty("sun.awt.noerasebackground", "true")
    }

    fun start() {
        init()

        isVisible = true

        thread {
            applicationLoop()
        }
    }

    // Abstract Methods

    abstract fun initWindow()
    abstract fun init()
    abstract fun render(g: Graphics2D)
    abstract fun update()

    // Rendering

    var interpolation = 1.0
    internal var frameCount = 0
    var fps = 0

    val timeBetweenUpdates: Int
        get() = 1000000000 / 30

    val timeBetweenRenders: Int
        get () = 1000000000 / 60

    internal var lastUpdateTime = System.nanoTime()

    internal var lastRenderTime = System.nanoTime()

    internal var lastSecondTime = (lastUpdateTime / 1000000000).toInt()

    var maxUpdatesBeforeRender = 5
    var showFPS = true
    // var pauseRendering = false
    var totalUpdates = 0

    fun applicationLoop() {

        while (isVisible) {

            var now = System.nanoTime()
            var updateCount = 0

            while (now - lastUpdateTime > timeBetweenUpdates && updateCount < maxUpdatesBeforeRender) {
                update()
                lastUpdateTime += timeBetweenUpdates
                updateCount++

                totalUpdates++
            }

            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if (now - lastUpdateTime > timeBetweenUpdates) {
                lastUpdateTime = now - timeBetweenUpdates
            }

            //Render. To do so, we need to calculate interpolation for a smooth render.
            interpolation = Math.min(1.0, ((now - lastUpdateTime) / timeBetweenUpdates).toDouble())
            renderInternal()
            lastRenderTime = now

            //Update the frames we got.
            val thisSecond = (lastUpdateTime / 1000000000).toInt()

            if (thisSecond > lastSecondTime) {
                if (showFPS) System.out.println("$frameCount FPS")
                fps = frameCount
                frameCount = 0
                lastSecondTime = thisSecond
            }

            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while (now - lastRenderTime < timeBetweenRenders && now - lastUpdateTime < timeBetweenUpdates) {

                Thread.`yield`()

                //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.

                Thread.sleep(1)

                now = System.nanoTime()
            }

        }
    }

    internal var buffer: BufferedImage? = null

    internal fun renderInternal() {

        if (buffer == null) {
            buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        }

        val g = buffer?.graphics as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        render(g)

        graphics.drawImage(buffer, insets.left, insets.top, null)

        frameCount++
    }

    // Binding properties

    var resizeable: Boolean
        get() = isResizable
        set(value) {
            isResizable = value
        }

    var size: WindowSize
        get() = WindowSize(width, height)
        set(value) {
            setSize(
                if (value.width < 0) width else value.width,
                if (value.height < 0) height else value.height
            )
        }

    // FIX FOR RESIZE FLICKERING //

    override fun update(g: Graphics) { }

    override fun paint(g: Graphics?) { }

}

data class WindowSize(val width: Int = -1, val height: Int = -1)

fun size(width: Int, height: Int) = WindowSize(width, height)
