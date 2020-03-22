package org.araqnid.kotlin.hoistregex.kotlin

class PatternAllocator {
    private val allocated = mutableMapOf<String, MutableList<Allocated>>()

    val all: List<Allocated>
        get() = allocated.values.flatten()

    fun allocate(className: String, pattern: String): Allocated {
        val forClass = allocated.getOrPut(className) { mutableListOf() }
        val symbol = "\$pattern\$${forClass.size}"
        val allocated = Allocated(className, symbol, pattern)
        forClass += allocated
        return allocated
    }

    fun allocatedForClass(className: String): List<Allocated> {
        return allocated[className] ?: emptyList()
    }

    data class Allocated(val className: String, val symbol: String, val pattern: String)
}
