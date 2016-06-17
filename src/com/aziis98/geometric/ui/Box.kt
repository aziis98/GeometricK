package com.aziis98.geometric.ui

import com.aziis98.deluengine.maths.Vec2i
import com.aziis98.geometric.ui.feature.*
import com.aziis98.geometric.util.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

val ABSENT:PackedInt
    get() = PackedInt(0, false)

val ZERO: PackedInt
    get() = PackedInt(0, true)

const val NO_ID = "__noId__"

open class Box(val container: IPackSized,
               var left:   PackedInt = ABSENT,
               var right:  PackedInt = ABSENT,
               var top:    PackedInt = ABSENT,
               var bottom: PackedInt = ABSENT,
               override var width:  PackedInt = ABSENT,
               override var height: PackedInt = ABSENT,
               val id: String = NO_ID) : IPackSized {

    val features = PriorityList<Feature>()
    val children = ArrayList<Box>()

    fun updateLayout() {

        featuresOfType<ConstraintFeature>().forEach(ConstraintFeature::updateConstraint)

        when {
            left.isAbsent() -> left.value = container.width - right - width
            right.isAbsent() -> right.value = container.width - left - width
            width.isAbsent() -> width.value = container.width - left - right
        }

        when {
            top.isAbsent() -> top.value = container.height - bottom - height
            bottom.isAbsent() -> bottom.value = container.height - top - height
            height.isAbsent() -> height.value = container.height - top - bottom
        }

        children.forEach(Box::updateLayout)
    }

    inline fun <reified F> featuresOfType() = features.filterIsInstance<F>()
    inline fun <reified F> featureOfType() = features.first { it is F } as F

    fun query(id: String): Box {
        return children.first { it.id == id }
    }

    override fun toString(): String {
        return "Box(id = $id, left = $left, right = $right, top = $top, bottom = $bottom, width = $width, height = $height)"
    }

    val parent: Box?
        get() = container as? Box

}

fun Box.contains(rel: Vec2i): Boolean {
    return rel.x >= 0 && rel.y >= 0 && rel.x <= width.toInt() && rel.y <= height.toInt()
}

val Box.absoluteLeft: Int
    get() = (parent?.absoluteLeft ?: 0) + left.toInt()

val Box.absoluteTop: Int
    get() = (parent?.absoluteTop ?: 0) + top.toInt()

fun Box.toRelativeCoord(absPosition: Vec2i): Vec2i {
    return absPosition - Vec2i(absoluteLeft, absoluteTop)
}