package com.aziis98.geometric.ui

import com.aziis98.geometric.ui.feature.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

interface ISized {
    val width: PackedInt
    val height: PackedInt
}

fun Box.box(left: PackedInt = ABSENT,
        right: PackedInt = ABSENT,
        top: PackedInt = ABSENT,
        bottom: PackedInt = ABSENT,
        width: PackedInt = ABSENT,
        height: PackedInt = ABSENT,
        id: String = NO_ID) = Box(this, left, right, top, bottom, width, height, id)

val ABSENT:PackedInt
    get() = PackedInt(0, false)

val ZERO: PackedInt
    get() = PackedInt(0, true)

const val NO_ID = "__noId__"

open class Box(val container: ISized,
               var left:   PackedInt = ABSENT,
               var right:  PackedInt = ABSENT,
               var top:    PackedInt = ABSENT,
               var bottom: PackedInt = ABSENT,
               override var width:  PackedInt = ABSENT,
               override var height: PackedInt = ABSENT,
               val id: String = NO_ID) : ISized {

    fun updateLayout() {

        featuresOfType<LayoutConstraintFeature>().forEach(LayoutConstraintFeature::updateConstraint)

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

        children.forEach { it.updateLayout() }
    }

    val features = ArrayList<Feature>()
    val children = ArrayList<Box>()

    inline fun <reified F> featuresOfType(): Collection<F> {
        // println("Retriving feature of type: ${F::class.simpleName}")
        return features /* .apply { println(this) } */ .filterIsInstance<F>() // .apply { println(this) }
    }

    companion object {

        fun fillContainer(container: Box) = Box(container, ZERO, ZERO, ZERO, ZERO)

    }

    override fun toString(): String {
        return """Box(left = $left, right = $right, top = $top, bottom = $bottom, width = $width, height = $height) [
        |   ${ if (children.isEmpty()) "" else children.map { "\t" + it.toString() + "\n" }.reduce { a, b -> a + b }}
        |]
        """.trimMargin()
    }

}

class PackedInt(var value: Int, val present: Boolean) {
    fun isAbsent() = !present
    fun isPresent() = present

    fun toInt() = value

    override fun toString() = "${if (present) "V" else "X"}$value"
}

val Int.pk: PackedInt
    get() = PackedInt(this, true)

// Makes [PackedInt] work with normal Ints in addition and subtractions

operator fun PackedInt.plus(other: PackedInt) = value + other.value
operator fun PackedInt.plus(other: Int) = value + other
operator fun Int.plus(other: PackedInt) = this + other.value

operator fun PackedInt.minus(other: PackedInt) = value - other.value
operator fun PackedInt.minus(other: Int) = value - other
operator fun Int.minus(other: PackedInt) = this - other.value

