package com.aziis98.geometric.util

// Copyright 2016 Antonio De Lucreziis

class PackedInt(var value: Int, val present: Boolean) {
    fun isAbsent() = !present
    fun isPresent() = present

    fun toInt() = value

    override fun toString() = if (present) "$value" else "gen($value)"
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