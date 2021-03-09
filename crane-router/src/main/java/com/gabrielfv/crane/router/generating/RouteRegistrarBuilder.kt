package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XFiler

interface RouteRegistrarBuilder {

  fun build(
    filer: XFiler,
    className: String,
    routes: Map<String, String>
  )
}
