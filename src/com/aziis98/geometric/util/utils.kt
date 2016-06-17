package com.aziis98.geometric.util

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import kotlin.concurrent.thread

// Copyright 2016 Antonio De Lucreziis

operator fun <E> MutableCollection<E>.plusAssign(element: E) {
    this.add(element)
}

operator fun <E> PriorityList<E>.plusAssign(element: E) {
    this.add(element)
}

infix fun <E> E.addTo(collection: MutableCollection<E>): E {
    collection.add(this)
    return this
}

infix fun <E> E.addTo(prioriryList: PriorityList<in E>): E {
    prioriryList.add(this)
    return this
}

fun Graphics2D.drawStringCentered(string: String, x: Int, y: Int) : Rectangle2D {
    val bounds = fontMetrics.getStringBounds(string, this)
    drawString(string, (x - bounds.width / 2).toInt(), y + (bounds.height / 2).toInt())
    return bounds
}

fun Box.tryRender(g: Graphics2D) = tryCallOn<RenderFeature> {
    it.render(g)
}

inline fun <reified F> Box.tryCallOn(action: (F)->Unit) {
    featuresOfType<F>().forEach(action)
}

fun runLater(delay: Long, action: () -> Unit): () -> Unit {
    var cancelled = false

    thread {
        Thread.sleep(delay)

        if (!cancelled) action()
    }

    return {
        cancelled = true
    }
}

class FormattedStringBuffer(val indentString: String = "  ") {
    internal var indent = 0
    val sb = StringBuilder()

    fun indent() {
        indent++
    }

    fun deindent() {
        indent--
    }

    fun indented(block: () -> Unit) {
        indent()
        block()
        deindent()
    }

    fun appendIndentation() {
        for (i in 1 .. indent) {
            sb.append(indentString)
        }
    }

    fun appendln(any: Any) {
        appendIndentation()
        sb.append(any)
        sb.append("\n")
    }

    override fun toString() = sb.toString()
}

fun <T> printfRec(obj: T, fsb: FormattedStringBuffer = FormattedStringBuffer(), doPrint: Boolean = true, printer: (FormattedStringBuffer, T, (T) -> Unit) -> Unit) {

    printer(fsb, obj) { subObj ->
        printfRec(subObj, fsb, false, printer)
    }

    if (doPrint) println(fsb)
}

// DSL TEST

infix fun (() -> Unit).iff(condition: Boolean) {
    if (condition)
        this()
}

// GRAPHICS2D
//

fun Graphics2D.drawCircle(x: Int, y: Int, radius: Int) {
    drawOval(x - radius, y - radius, radius * 2, radius * 2)
}

fun Graphics2D.fillCircle(x: Int, y: Int, radius: Int) {
    fillOval(x - radius, y - radius, radius * 2, radius * 2)
}

//
