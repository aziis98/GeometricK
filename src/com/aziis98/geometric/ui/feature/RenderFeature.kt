package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box
import java.awt.Graphics2D


// Copyright 2016 Antonio De Lucreziis

abstract class RenderFeature(owner: Box) : Feature(owner) {

    abstract fun render(g: Graphics2D)

}