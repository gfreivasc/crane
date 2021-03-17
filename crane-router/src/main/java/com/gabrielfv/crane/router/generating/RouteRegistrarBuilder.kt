package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XTypeElement

internal interface RouteRegistrarBuilder {

  fun build(
    filer: XFiler,
    className: String,
    routes: Map<String, String>,
    originating: Set<XTypeElement>
  )
}
