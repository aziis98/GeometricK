package com.aziis98.geometric.ui

import com.aziis98.geometric.window.Window

// Copyright 2016 Antonio De Lucreziis

class JFrameContainer(val window: Window) : ISized {
    override val width: Int
        get() = window.width
    override val height: Int
        get() = window.height

}

class WindowUI(window: Window) : Box(JFrameContainer(window), 0, 0, 0, 0)
