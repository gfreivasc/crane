package com.gabrielfv.crane.core.affinity

import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import kotlin.reflect.KClass

abstract class AffinityRoute(destination: KClass<out Fragment>) : Route(destination) {
  val tag: String get() = hashCode().toString()
}
