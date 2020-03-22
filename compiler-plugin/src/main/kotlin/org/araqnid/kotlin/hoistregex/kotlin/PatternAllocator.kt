package org.araqnid.kotlin.hoistregex.kotlin

class PatternAllocator {
    private val allocated = mutableMapOf<String, MutableList<Allocated>>()

    val all: List<Allocated>
        get() = allocated.values.flatten()

    fun allocate(className: String, pattern: Pattern): Allocated {
        val forClass = allocated.getOrPut(className) { mutableListOf() }
        val alreadyExists = forClass.find { it.pattern == pattern }
        if (alreadyExists != null) return alreadyExists
        val symbol = "\$pattern\$${forClass.size}"
        val allocated = Allocated(className, symbol, pattern)
        forClass += allocated
        return allocated
    }

    fun allocatedForClass(className: String): List<Allocated> {
        return allocated[className] ?: emptyList()
    }

    data class Pattern(val source: String, val options: Set<RegexOption>)
    data class Allocated(val className: String, val symbol: String, val pattern: Pattern)
}
