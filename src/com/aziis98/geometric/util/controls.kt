package com.aziis98.geometric.util

import com.aziis98.geometric.ui.*
import com.aziis98.geometric.ui.feature.render.renderChildren

// Copyright 2016 Antonio De Lucreziis

fun Box.box(left: PackedInt = ABSENT,
            right: PackedInt = ABSENT,
            top: PackedInt = ABSENT,
            bottom: PackedInt = ABSENT,
            width: PackedInt = ABSENT,
            height: PackedInt = ABSENT,
            id: String = NO_ID,
            block: Box.() -> Unit = {}) = Box(this, left, right, top, bottom, width, height, id).apply(block)

fun Box.control(left: PackedInt = ABSENT,
                right: PackedInt = ABSENT,
                top: PackedInt = ABSENT,
                bottom: PackedInt = ABSENT,
                width: PackedInt = ABSENT,
                height: PackedInt = ABSENT,
                id: String = NO_ID,
                block: Box.() -> Unit = {}) = Box(this, left, right, top, bottom, width, height, id)
    .apply {
        features.add(renderChildren(), PRIORITY_LOWEST)
        block()
    }
