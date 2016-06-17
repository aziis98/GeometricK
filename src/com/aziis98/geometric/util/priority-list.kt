package com.aziis98.geometric.util

import java.util.*

// Copyright 2016 Antonio De Lucreziis

const val PRIORITY_LOWEST = -2
const val PRIORITY_LOW = -1
const val PRIORITY_NORMAL = 0
const val PRIORITY_HIGH = 1
const val PRIORITY_HIGHEST = 2


class PriorityList<T>() : Iterable<T> {

    internal val treeSet = TreeSet<PriorityElement<T>>() { a, b ->
        if (a.priority == b.priority)
            1
        else
            b.priority - a.priority
    }

    fun add(element: T, priority: Int = PRIORITY_NORMAL) {
        treeSet.add(PriorityElement(element, priority))
    }

    @Suppress("UNCHECKED_CAST")
    override fun iterator(): Iterator<T> = PriorityIterator(treeSet.iterator())

    internal class PriorityIterator<T>(val iterator: Iterator<PriorityElement<T>>) : Iterator<T> {
        override fun hasNext() = iterator.hasNext()
        override fun next() = iterator.next().element
    }

}

internal data class PriorityElement<T>(val element: T, val priority: Int)