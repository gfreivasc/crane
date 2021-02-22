package com.gabrielfv.crane.core.result

import android.os.Bundle
import android.os.Parcelable
import com.gabrielfv.crane.util.Saveable
import kotlin.reflect.KClass

internal class ResultRegistry : Saveable {
    private val results = mutableListOf<Parcelable>()

    fun <T : Parcelable> push(result: T) {
        results.find { result::class.isInstance(it) }
            ?.also { results.remove(it) }
        results.add(result)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Parcelable> fetch(cls: KClass<T>): T? {
        return results
            .find { cls.isInstance(it) }
            ?.let { it as T }
            ?.also { results.remove(it) }
    }

    override fun save(outState: Bundle) {
        if (results.isNotEmpty()) {
            outState.putParcelableArray(RESULT_REGISTRY, results.toTypedArray())
        }
    }

    override fun restore(savedState: Bundle) {
        val saved = savedState.getParcelableArray(RESULT_REGISTRY)
        if (saved != null) {
            results.clear()
            results.addAll(saved)
        }
    }

    companion object {
        const val RESULT_REGISTRY = "com.gabrielfv.crane.RESULT_REGISTRY"
    }
}
