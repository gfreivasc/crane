package com.gabrielfv.crane.router

import com.gabrielfv.crane.core.RouteMap

interface RouteRegistrar {
  fun get(): RouteMap
}
