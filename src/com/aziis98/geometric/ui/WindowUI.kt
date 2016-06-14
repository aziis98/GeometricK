package com.aziis98.geometric.ui

import com.aziis98.geometric.window.Window

// Copyright 2016 Antonio De Lucreziis

class JFrameContainer(val window: Window) : ISized {
    override val width: PackedInt
        get() = window.width.pk
    override val height: PackedInt
        get() = window.height.pk

}

class WindowUI(window: Window) : Box(JFrameContainer(window), ZERO, ZERO, ZERO, ZERO)

