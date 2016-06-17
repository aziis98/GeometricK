package com.aziis98.geometric.ui.feature

import com.aziis98.geometric.ui.Box
import com.aziis98.geometric.util.pk

// Copyright 2016 Antonio De Lucreziis

class LayoutConstraintFeature(override val owner: Box, val constraint: Box.()->Unit,
                              override var disabled: Boolean = false) : ConstraintFeature {
    override fun updateConstraint() {
        if (disabled) return

        owner.constraint()
    }
}

fun Box.constraint(constraint: Box.() -> Unit) = LayoutConstraintFeature(this, constraint)


class ConstraintCenterFeature(override val owner: Box,
                              val horizontaly: Boolean = true,
                              val vertically: Boolean = true,
                              override var disabled: Boolean = false) : ConstraintFeature {
    override fun updateConstraint() {
        if (disabled) return

        if (horizontaly) {
            owner.left = ((owner.container.width.toInt() - owner.width.toInt()) / 2).pk
        }
        if (vertically) {
            owner.top = ((owner.container.height.toInt() - owner.height.toInt()) / 2).pk
        }
    }
}

fun Box.constraintAlign(horizontaly: Boolean = false, vertically: Boolean = false) = ConstraintCenterFeature(this, horizontaly, vertically)