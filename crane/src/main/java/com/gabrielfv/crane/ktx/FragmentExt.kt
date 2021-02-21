package com.gabrielfv.crane.ktx

import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.core.Crane
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import kotlin.reflect.KProperty

fun Fragment.params() = ParamsDelegate()

class ParamsDelegate internal constructor() {
    operator fun <T : Route> getValue(thisRef: Fragment, prop: KProperty<*>): T {
        return thisRef.arguments?.getParcelable(Crane.KEY_CRANE_PARAMS)
            ?: throw IllegalStateException(
                "Tried to access a route that did not exist. " +
                        "Has the fragment been spawned through <com.gabrielfv.crane.Crane>?"
            )
    }
}
