package com.gabrielfv.crane.core.affinity

import android.os.Bundle
import com.gabrielfv.crane.util.Saveable

private typealias Tag = String
private typealias Offset = Int



internal class AffinityManager : Saveable {
    private val affinityOffset = mutableMapOf<Tag, Offset>()

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

    override fun save(outState: Bundle) {
        outState.putSerializable(AFFINITY_REGISTRY, HashMap(affinityOffset))
    }

    @Suppress("UNCHECKED_CAST")
    override fun restore(savedState: Bundle) {
        val out = try {
            savedState
                .getSerializable(AFFINITY_REGISTRY) as? HashMap<Tag, Offset>?
        } catch (ex: ClassCastException) {
            null
        }

        if (out != null) {
            affinityOffset.putAll(out)
        }
    }

    private fun offsetAllBy(offset: Int) {
        affinityOffset.forEach { entry ->
            affinityOffset[entry.key] = entry.value + offset
        }
    }

    companion object {
        const val AFFINITY_REGISTRY = "com.gabrielfv.crane.AFFINITY_REGISTRY"
    }
}
