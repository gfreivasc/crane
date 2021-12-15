package com.gabrielfv.crane.core

import androidx.annotation.MainThread

interface CraneRegistry {

  @MainThread
  fun create(
    routeMap: RouteMap,
    factory: Crane.Factory = Crane.Factory()
  ): Crane

  @MainThread
  fun getInstance(): Crane

  class Default : CraneRegistry {
    private lateinit var instance: Crane

    override fun create(routeMap: RouteMap, factory: Crane.Factory): Crane {
      return factory.create(routeMap)
    }

    override fun getInstance(): Crane {
      check(::instance.isInitialized) {
        "Instance of Crane not available, make sure it has been initialized."
      }
      return instance
    }
  }
}
