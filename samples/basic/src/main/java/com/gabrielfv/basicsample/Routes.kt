package com.gabrielfv.basicsample

import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.core.RouteMap
import kotlinx.parcelize.Parcelize

sealed interface Routes : Route {
  @Parcelize
  object First : Routes

  @Parcelize
  data class Second(val count: Int) : Routes
}

val routeMap: RouteMap get() = mapOf(
  Routes.First::class to FirstFragment::class,
  Routes.Second::class to SecondFragment::class
)
