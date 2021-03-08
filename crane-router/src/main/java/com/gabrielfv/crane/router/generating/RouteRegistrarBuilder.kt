package com.gabrielfv.crane.router.generating

import javax.annotation.processing.Filer

interface RouteRegistrarBuilder {

  fun build(
    filer: Filer,
    className: String,
    routes: Map<String, String>
  )
}
