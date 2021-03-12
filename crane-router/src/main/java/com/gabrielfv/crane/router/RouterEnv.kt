package com.gabrielfv.crane.router

import com.squareup.kotlinpoet.ClassName

internal object RouterEnv {
  const val ROUTER_CLASS = "Router"
  const val GETTER_METHOD = "get"
  const val REGISTRARS_PACKAGE = "com.gabrielfv.crane.routes.registrar"
  val registrarInterfaceName get() =
    ClassName(
      "com.gabrielfv.crane.router",
      "RouteRegistrar"
    )
  val routeMapName get() =
    ClassName(
      "com.gabrielfv.crane.core",
      "RouteMap"
    )
  val routeName get() =
    ClassName(
      "com.gabrielfv.crane.core",
      "Route"
    )
  val fragmentName get() =
    ClassName(
      "androidx.fragment.app",
      "Fragment"
    )
}
