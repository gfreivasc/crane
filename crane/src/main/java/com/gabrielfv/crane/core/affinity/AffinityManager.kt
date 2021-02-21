package com.gabrielfv.crane.core.affinity

private typealias FragmentTag = String
private typealias NavOffset = Int

internal class AffinityManager {
    private val affinityOffset = mutableMapOf<FragmentTag, NavOffset>()

    fun push(tag: String? = null) {
        offsetAllBy(1)
        if (tag != null) affinityOffset[tag] = 1
    }

    fun popRegular() {
        affinityOffset.forEach { entry ->
            val new = entry.value - 1
            if (new == 0) affinityOffset.remove(entry.key)
            else affinityOffset[entry.key] = new
        }
    }

    fun popAffinity(): Pair<String, Int>? {
        return affinityOffset.minByOrNull { it.value }
            ?.also { removed ->
                affinityOffset.remove(removed.key)
                offsetAllBy(-removed.value)
            }
            ?.toPair() ?: null.also { popRegular() }
    }

    private fun offsetAllBy(offset: Int) {
        affinityOffset.forEach { entry ->
            affinityOffset[entry.key] = entry.value + offset
        }
    }
}
