package com.aziis98.geometric.ui.feature.input

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.ui.feature.Feature

// Copyright 2016 Antonio De Lucreziis

class FocusFeature(override val owner: Box) : Feature {
    var focussed = false
        set(value) {
            if (value) {
                owner.parent?.children?.forEach {
                    if (it != owner)
                        it.featureOfType<FocusFeature>()?.focussed = false
                }
            }
            field = value
        }
}

fun Box.focussable() = FocusFeature(this)