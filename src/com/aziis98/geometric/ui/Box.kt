package com.aziis98.geometric.ui

import com.aziis98.geometric.ui.feature.Feature
import java.util.*

// Copyright 2016 Antonio De Lucreziis

interface ISized {
    val width: Int
    val height: Int
}

const val ABSENT = -1

open class Box(val container: ISized,
               var left: Int = ABSENT, var right: Int = ABSENT, var top: Int = ABSENT, var bottom: Int = ABSENT,
               override var width: Int = ABSENT, override var height: Int = ABSENT) : ISized {

    init {

    }

    val features = ArrayList<Feature>()
    val children = ArrayList<Box>()

    inline fun <reified F> featuresOfType(): Collection<F> {
        return features.filterIsInstance<F>()
    }

    companion object {

        fun fillContainer(container: Box) = Box(container, 0, 0, 0, 0)

    }

}