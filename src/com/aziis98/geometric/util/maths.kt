package com.aziis98.geometric.util

import com.aziis98.deluengine.maths.Vec2d

// Copyright 2016 Antonio De Lucreziis

/**
 * Determinant and Matrices in this function present matrix form for readablility
 *
 * Example:
 * ```
 * val test = det(a, b,
 * /*          */ c, d)
 * ```
 * The comment is not present in the code
 */
@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.FUNCTION)
annotation class MatrixForm

// @formatter:off

/**
 * Solves a linear 2x2 system with cramer's rule
 */
@MatrixForm
fun solve(a1: Double, b1: Double, c1: Double,
          a2: Double, b2: Double, c2: Double) : Vec2d {

    val delta = det(a1, b1,
                    a2, b2)

    return Vec2d(det(c1, b1,
                     c2, b2) / delta, det(a1, c1,
                                          a2, c2) / delta)
}

@MatrixForm
fun det(a: Double, b: Double,
        c: Double, d: Double) = a * d - b * c

// @formatter:on

fun Double.nearEquals(value: Double, uncertainty: Double = 0.01) : Boolean = Math.abs(this - value) <= uncertainty