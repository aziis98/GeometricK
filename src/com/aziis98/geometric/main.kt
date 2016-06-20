package com.aziis98.geometric

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.Geometric.toolbarMenu
import com.aziis98.geometric.event.GeometricEvents
import com.aziis98.geometric.renderer.*
import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.*
import com.aziis98.geometric.ui.feature.input.*
import com.aziis98.geometric.ui.feature.render.*
import com.aziis98.geometric.ui.feature.render.RenderTextureFeature.TextureRenderMode
import com.aziis98.geometric.util.*
import com.aziis98.geometric.window.*
import com.aziis98.geometric.window.Window
import java.awt.*
import kotlin.properties.Delegates

// Copyright 2016 Antonio De Lucreziis

fun main(args: Array<String>) {
    Geometric.start()
}

val COLOR_BACKGROUND = Color(0xFFFFFF)
val COLOR_TOOLBAR_LABEL = Color(0x777777)
val COLOR_TOOLBAR_LABEL_HOVER = Color(0x88bbbbbb.toInt(), true)

val TEXTURE_TOOLBAR = TextureLoader.ninePatch("/ui/light-toolbar.png")
val TEXTURE_STATUSBAR = TextureLoader.ninePatch("/ui/light-statusbar.png")
val TEXTURE_CONTEXTMENU = TextureLoader.ninePatch("/ui/context-menu.png")

object Geometric : Window() {

    val ui: WindowUI by lazy { WindowUI(this) }
    var renderer: Renderer by Delegates.notNull()

    override fun initWindow() {
        size = size(1000, 800)
        title = "Geometric"
        resizeable = true
    }

    fun Box.toolbarMenu(id: String, label: String, previousMenu: Box? = null): Box {
        return control(id = id, left = 0.pk, top = 0.pk, height = 27.pk) {
            renderText(label, COLOR_TOOLBAR_LABEL, offset = Vec2i(0, -4)) { w, h ->
                width = (w + 20).pk
            } addTo features

            val hoverF = renderize { g ->
                g.color = COLOR_TOOLBAR_LABEL_HOVER
                g.fillRect(0, 0, width.toInt(), height.toInt() - 2)
            }
            features.add(hoverF, PRIORITY_HIGH)
            hoverF.disabled = true

            if (previousMenu != null) {
                features += constraint {
                    left = (previousMenu.left + previousMenu.width + 1).pk
                }
            }

            // features += inputHover({ textF.color = COLOR_TOOLBAR_LABEL_HOVER }, { textF.color = COLOR_TOOLBAR_LABEL })
            features += inputHover({ hoverF.disabled = false }, { hoverF.disabled = true })

            // invalidate()
        }
    }

    fun Box.tool(id: String, icon: String, previousTool: Box? = null): Box {
        return control(id = id, left = 4.pk, width = 45.pk, height = 45.pk) {
            features += renderTexture(TextureLoader.texture("/tools/$icon.png"), TextureRenderMode.Strech)
            features += constraintAlign(vertically = true)

            val hoverF = renderize { g ->
                g.color = COLOR_TOOLBAR_LABEL_HOVER
                g.fillRect(0, 0, width.toInt(), height.toInt())
            }
            features.add(hoverF, PRIORITY_HIGH)
            hoverF.disabled = true

            if (previousTool != null) {
                features += constraint {
                    left = (previousTool.left + previousTool.width + 4).pk
                }
            }

            features += inputHover({ hoverF.disabled = false }, { hoverF.disabled = true })
            features += inputClick { GeometricEvents.toolSelected(id) }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    override fun init() {
        ui.apply {
            features += renderChildren()

            // @formatter:off

            // RENDERER
            children += box(left = 0.pk, right = 0.pk, top = 82.pk, bottom = 28.pk, id = "renderer") {
                features += Renderer(this).apply {
                    renderer = this
                    registerHandlers()
                }
            }

            // TOOLBAR
            ui.initializeMenubar()

            // TOOLS
            children += control(id = "tools", left = 0.pk, right = 0.pk, top = 27.pk, height = 54.pk) {
                features += renderNinePatch(TEXTURE_TOOLBAR)

                val toolPoint     = tool(TOOL_POINT, "point") addTo children
                val toolCentroid  = tool(TOOL_CENTROID, "point-centroid", toolPoint) addTo children
                val toolLine      = tool(TOOL_LINE, "line", toolCentroid) addTo children
                val toolLineParal = tool(TOOL_LINE_PARALLEL, "line-parallel", toolLine) addTo children
                val toolLinePerp  = tool(TOOL_LINE_PERPENDICULAR, "line-perpendicular", toolLineParal) addTo children
                val toolLineInter = tool(TOOL_LINE_INTERSECTION, "line-intersection", toolLinePerp) addTo children

            }

            // STATUSBAR
            children += control(id="statusbar", left = 0.pk, right = 0.pk, bottom = 0.pk, height = 27.pk) {
                features += renderNinePatch(TEXTURE_STATUSBAR)

                children += control(id = "status-tool",right = 0.pk, height = 27.pk) {
                    features += constraintAlign(vertically = true)
                    val textF = renderText("TOOL: $TOOL_NONE", COLOR_TOOLBAR_LABEL, offset = Vec2i(0,-2)) { w, h ->
                        width = (w + 20).pk
                    } addTo features
                }
            }

            // @formatter:on
        }

        ui.updateLayout()
        ui.invalidate()

        WindowEvents.on<WindowResized> {
            ui.invalidate()
        }

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
        g.background = COLOR_BACKGROUND
        g.clearRect(0, 0, width, height)

        ui.tryRender(g)
    }

    override fun update() {
        ui.updateLayout()
    }

}

@Suppress("UNUSED_VARIABLE")
fun WindowUI.initializeMenubar() {
    children += control(id = "menubar", left = 0.pk, right = 0.pk, top = 0.pk, height = 27.pk) {
        features += renderNinePatch(TEXTURE_TOOLBAR)

        val toolbarFile = toolbarMenu("menubar-file", "File")               addTo children
        val toolbarEdit = toolbarMenu("menubar-edit", "Edit", toolbarFile)  addTo children
        var toolbarHelp = toolbarMenu("menubar-help", "Help", toolbarEdit)  addTo children

        toolbarFile.apply {
            contextmenu("contextmenu-file") {
                context_button("New") { println("New") }
                context_button("Save") { println("Saving the file") }
                context_button("Save As...") { println("Saving the file as") }
                context_button("Open") { println("Open") }
            }
        }
    }
}

fun Box.contextmenu(id: String, block: Box.() -> Unit): Box {

    val contextmenu = control(id = id, left = 100.pk, top = 100.pk, height = 0.pk) {
        features += renderNinePatch(TEXTURE_CONTEXTMENU)
        features += constraint {
            width = (children.map { it.width.toInt() }.max() ?: 0).pk
            height = (children.size * 27).pk
        }

        this.apply(block)

        disabled = true
    }

    features += inputClick {

        contextmenu.disabled = false
        contextmenu.left = 0.pk
        contextmenu.top = this@contextmenu.height
        contextmenu.invalidate()
    }

    return contextmenu
}

fun Box.context_button(label: String, id: String = NO_ID, onClick: () -> Unit) = control(id = id, left = 0.pk, height = 27.pk) {
    features += renderText(label, COLOR_TOOLBAR_LABEL) { w, h ->
        width = Math.max(width.toInt(), w).pk
    }
    features += inputClick { onClick() }

    val index = this@context_button.children.size
    features += constraint {
        top = (index * 27).pk
    }

    this@context_button.children += this
}

/*
fun WindowUI.showContextMenu(id: String, location: Vec2i) {
    query(id)?.apply {
        disabled = false
        left = (location.x - (parent?.absoluteLeft ?: 0)).pk
        top  = (location.y - (parent?.absoluteTop  ?: 0)).pk
        invalidate()
    }
}

fun WindowUI.hideContextMenu(id: String) {
    query(id)?.disabled = true
}
*/


