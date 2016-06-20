package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.util.tryRender
import java.awt.*


// Copyright 2016 Antonio De Lucreziis

class RenderChildrenFeature(override val owner: Box) : RenderFeature() {
    override fun render(g: Graphics2D) {
        if (disabled) return

        owner.children.forEach {
            g.translate(it.left.value, it.top.value)

            val hasRenderClip = it.featureOfType<ClipRenderFeature>() != null

            val bufferClip = g.getClipBounds(Rectangle())

            if (hasRenderClip) {
                g.clip = null
                it.tryRender(g)
            }
            else {
                g.setClip(0, 0, it.width.toInt(), it.height.toInt())
                it.tryRender(g)
            }

            g.clip = bufferClip

            g.translate(-it.left.value, -it.top.value)
        }
    }
}

fun Box.renderChildren() = RenderChildrenFeature(this)