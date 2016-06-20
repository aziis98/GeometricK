package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

interface Feature {
    val owner: Box
}
/*
interface MouseInputFeature : Feature {
    fun onMove(position: Vec2i, previous: Vec2i) {
        owner.children.forEach {
            val insets = Vec2i(it.left.toInt(), it.top.toInt())

            val relPosition = position - insets
            val relPrevious = position - insets

            val posInside = it.contains(relPosition)
            val prevInside = it.contains(relPrevious)

            if (posInside || prevInside) {

                it.featuresOfType<MouseInputFeature>().forEach {
                    it.onMove(relPosition, relPrevious)
                }

            }

            if (posInside && !prevInside) {
                onEnter(relPosition, relPrevious)
            }
            else if (!posInside && prevInside) {
                onExit(relPosition, relPrevious)
            }
        }
    }

    fun onEnter(position: Vec2i, previous: Vec2i) { }

    fun onExit(position: Vec2i, previous: Vec2i) { }

    fun onClick(position: Vec2i, button: Int) {
        owner.children.forEach {
            it.featuresOfType<MouseInputFeature>().forEach {
                it.onClick(position, button)
            }
        }
    }

    fun onPress(position: Vec2i, button: Int) {
        owner.children.forEach {
            it.featuresOfType<MouseInputFeature>().forEach {
                it.onPress(position, button)
            }
        }
    }

    fun onRelease(position: Vec2i, button: Int) {
        owner.children.forEach {
            it.featuresOfType<MouseInputFeature>().forEach {
                it.onRelease(position, button)
            }
        }
    }
}

interface KeyboardInputFeature : Feature {
    fun onKeyTyped() { }

    fun onKeyDown() { }

    fun onKeyUp() { }
}
*/

abstract class RenderFeature(var disabled: Boolean = false) : Feature {
    abstract fun render(g: Graphics2D)
}

interface ConstraintFeature : Feature {
    fun updateConstraint()
}

@Target(AnnotationTarget.CLASS)
annotation class InternalFeature