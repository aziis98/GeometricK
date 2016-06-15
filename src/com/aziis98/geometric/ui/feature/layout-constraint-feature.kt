package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box

// Copyright 2016 Antonio De Lucreziis

@InternalFeature
class LayoutConstraintFeature(owner: Box, val constraint: Box.() -> Unit) : Feature(owner) {
    fun updateConstraint() {
        owner.constraint()
    }
}

fun Box.layoutConstraint(constraint: Box.() -> Unit) = LayoutConstraintFeature(this, constraint)