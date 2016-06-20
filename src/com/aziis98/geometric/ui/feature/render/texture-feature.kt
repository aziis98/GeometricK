package com.aziis98.geometric.ui.feature.render

import com.aziis98.geometric.*
import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.RenderFeature
import com.aziis98.geometric.ui.feature.render.RenderTextureFeature.TextureRenderMode
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

class RenderNinePatchFeature(override val owner: Box, val ninePatch: NinePatch) : RenderFeature() {
    override fun render(g: Graphics2D) {
        if (disabled) return

        g.drawNinePatchTexture(ninePatch, 0, 0, owner.width.toInt(), owner.height.toInt())
    }
}

fun Box.renderNinePatch(ninePatch: NinePatch) = RenderNinePatchFeature(this, ninePatch)


class RenderTextureFeature(override val owner: Box,
                           val texture: Texture,
                           val mode: TextureRenderMode = TextureRenderMode.Strech) : RenderFeature() {
    override fun render(g: Graphics2D) {
        if (disabled) return

        when (mode) {
            TextureRenderMode.Strech -> g.drawTexture(texture, 0, 0, owner.width.toInt(), owner.height.toInt())
            TextureRenderMode.Center -> g.drawTexture(texture, (owner.width.toInt() - texture.width) / 2, (owner.height.toInt() - texture.height) / 2)
            TextureRenderMode.TopLeft -> g.drawTexture(texture, 0, 0)
        }

    }

    enum class TextureRenderMode {
        Strech, Center, TopLeft
    }
}

fun Box.renderTexture(texture: Texture,
                      mode: TextureRenderMode = TextureRenderMode.Strech) = RenderTextureFeature(this, texture, mode)