package com.aziis98.geometric

import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.constraint
import com.aziis98.geometric.ui.feature.render.*
import com.aziis98.geometric.util.*
import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*

// Copyright 2016 Antonio De Lucreziis

fun main(args: Array<String>) {
    Geometric.start()
}

val COLOR_TOOLBAR_LABEL = Color(0xCCCCCC)

object Geometric : Window() {

    val ui: WindowUI by lazy { WindowUI(this) }

    override fun initWindow() {
        size = size(1000, 800)
        title = "Geometric"
        resizeable = true
    }

    fun Box.toolbarMenu(id: String, label: String, previousMenu: Box? = null): Box {
        return control(id = id, left = 0.pk, top = 0.pk, height = 18.pk) {
            features += renderText(label, COLOR_TOOLBAR_LABEL) { w, h ->
                width = (w + 20).pk
            }
            if (previousMenu != null) {
                features += constraint {
                    left = (previousMenu.left + previousMenu.width).pk
                }
            }
        }
    }

    override fun init() {
        ui.apply {
            features += renderChildren()

            children += control(id = "toolbar", left = 0.pk, right = 0.pk, top = 0.pk, height = 27.pk) {
                features += renderNinePatch(TextureLoader.ninePatch("/ui/toolbar.png"))

                val toolbarFile = toolbarMenu("toolbar-file", "File") addTo children
                val toolbarEdit = toolbarMenu("toolbar-edit", "Edit", toolbarFile) addTo children
                val toolbar3 = toolbarMenu("toolbar-3", "A-Long-Menu", toolbarEdit) addTo children
                val toolbar4 = toolbarMenu("toolbar-4", "Help", toolbar3) addTo children

            }
        }

        ui.updateLayout()

        printfRec<Box>(ui) { fsb, box, rec ->
            fsb.appendln("$box [")
            fsb.indented {
                box.children.forEach {
                    rec(it)
                }
            }
            fsb.appendln("]")
        }
    }

    override fun render(g: Graphics2D) {
        g.background = Color(0x333333)
        g.clearRect(0, 0, width, height)

        ui.tryRender(g)
    }

    override fun update() {
        ui.updateLayout()
    }

}
