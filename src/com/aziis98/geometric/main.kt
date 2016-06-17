package com.aziis98.geometric

import com.aziis98.deluengine.io.Mouse
import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.event.GeometricEvents
import com.aziis98.geometric.renderer.Renderer
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
val COLOR_TOOLBAR_LABEL = Color(0x888888)
val COLOR_TOOLBAR_LABEL_HOVER = Color(0x000000)

val TEXTURE_TOOLBAR = TextureLoader.ninePatch("/ui/light-toolbar.png")

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
                g.color = Color(0x88DDDDDD.toInt(), true)
                g.fillRect(0, 0, width.toInt(), height.toInt())
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
        }
    }

    fun Box.tool(id: String, icon: String, previousTool: Box? = null): Box {
        return control(id = id, left = 4.pk, width = 45.pk, height = 45.pk) {
            features += renderTexture(TextureLoader.texture("/tools/$icon.png"), TextureRenderMode.Strech)
            features += constraintAlign(vertically = true)

            val hoverF = renderize { g ->
                g.color = Color(0x88DDDDDD.toInt(), true)
                g.fillRect(0, 0, width.toInt(), height.toInt())
            }
            features.add(hoverF, PRIORITY_HIGH)
            hoverF.disabled = true

            if (previousTool != null) {
                features += constraint {
                    left = (previousTool.left + previousTool.width + 1).pk
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

            children += control(id = "toolbar", left = 0.pk, right = 0.pk, top = 0.pk, height = 27.pk) {
                features += renderNinePatch(TEXTURE_TOOLBAR)

                val toolbarFile = toolbarMenu("toolbar-file", "File")               addTo children
                val toolbarEdit = toolbarMenu("toolbar-edit", "Edit", toolbarFile)  addTo children
                var toolbarHelp = toolbarMenu("toolbar-help", "Help", toolbarEdit)  addTo children

            }

            children += control(id = "tools", left = 0.pk, right = 0.pk, top = 27.pk, height = 54.pk) {
                features += renderNinePatch(TEXTURE_TOOLBAR)

                val toolPoint    = tool("tool-point"   , "point")                        addTo children
                val toolCentroid = tool("tool-centroid", "point-centroid", toolPoint   ) addTo children
                val toolLine     = tool("tool-line"    , "line"          , toolCentroid) addTo children

            }

            children += box(left = 0.pk, right = 0.pk, top = 81.pk, bottom = 0.pk, id = "renderer") {
                renderer = Renderer(this)
                renderer.registerHandlers()
                features += renderer

                features += inputClick { GeometricEvents.canvasClicked(toRelativeCoord(Mouse.position.toVec2i() - Vec2i(Geometric.insets.left, Geometric.insets.top)) ) }
            }
            // @formatter:on
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
        g.background = COLOR_BACKGROUND
        g.clearRect(0, 0, width, height)

        ui.tryRender(g)
    }

    override fun update() {
        ui.updateLayout()
    }

}
