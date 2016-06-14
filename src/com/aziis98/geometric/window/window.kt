package com.aziis98.geometric.window

import java.awt.*
import javax.swing.*
import kotlin.concurrent.thread

// Copyright 2016 Antonio De Lucreziis

abstract class Window : Canvas() {

    val jframe: JFrame = JFrame()

    init {
        initWindow()

        jframe.contentPane.add(this)
        jframe.pack()

        jframe.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        jframe.setLocationRelativeTo(null)
        jframe.isVisible = true

        thread { applicationLoop() }
    }

    fun start() {

    }

    // Abstract Methods

    abstract fun initWindow()
    abstract fun init()
    abstract fun paint(g: Graphics2D)
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

    var freshBuffer = false

    var totalUpdates = 0

    fun applicationLoop() {
        while (jframe.isVisible) {

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
            render()
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

    internal fun render() {
        val bs = bufferStrategy
        if (bs == null || freshBuffer) {
            createBufferStrategy(3)
            freshBuffer = false
            return
        }

        val g = bs.drawGraphics as Graphics2D

        paint(g)

        g.dispose()
        bs.show()

        frameCount++
    }

    // Binding properties

    var title: String
        get() = jframe.title
        set(value) { jframe.title = value }

    var resizeable: Boolean
        get() = jframe.isResizable
        set(value) { jframe.isResizable = value }

    var size: WindowSize
        get() = WindowSize(width, height)
        set(value) {
            setSize(
                if (value.width  < 0) width  else value.width,
                if (value.height < 0) height else value.height
            )
        }

    /*
    override fun getWidth() = jframe.width

    override fun getHeight() = jframe.height

    fun setWidth(value: Int) {
        jframe.setSize(value, height)
    }

    fun setHeight(value: Int) {
        jframe.setSize(width, value)
    }
    */

}

data class WindowSize(val width: Int = -1, val height: Int = -1)

fun size(width: Int, height: Int) = WindowSize(width, height)
