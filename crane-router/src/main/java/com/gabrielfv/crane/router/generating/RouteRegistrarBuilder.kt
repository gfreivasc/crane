package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XFiler

internal interface RouteRegistrarBuilder {

  fun build(
    filer: XFiler,
    className: String,
    routes: Map<String, String>
  )
}
