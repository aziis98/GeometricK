package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box

// Copyright 2016 Antonio De Lucreziis

class LayoutConstraintFeature(override val owner: Box, val constraint: Box.()->Unit,
                              override var disabled: Boolean = false) : ConstraintFeature {
    override fun updateConstraint() {
        if (disabled) return

        owner.constraint()
    }
}

fun Box.constraint(constraint: Box.() -> Unit) = LayoutConstraintFeature(this, constraint)