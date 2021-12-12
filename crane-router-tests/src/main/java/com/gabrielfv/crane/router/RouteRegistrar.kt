package com.gabrielfv.crane.router

import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import kotlin.reflect.KClass

interface RouteRegistrar {
  fun get(): Map<KClass<out Route>, KClass<out Fragment>>
}
