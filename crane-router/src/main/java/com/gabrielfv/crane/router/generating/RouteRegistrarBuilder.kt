package com.gabrielfv.crane.router.generating

import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.RouterEnv
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

class RouteRegistrarBuilder {

  fun build(
    dir: File,
    className: String,
    routes: Map<String, String>
  ) {
    if (routes.isEmpty()) return
    val fileBuilder = FileSpec.builder(RouterEnv.REGISTRARS_PACKAGE, className)
    val registrar = TypeSpec.objectBuilder(className)
      .addAnnotation(RouteRegistrar::class)
      .addSuperinterface(RouterEnv.registrarInterfaceName)
      .addFunction(
        FunSpec.builder(RouterEnv.GETTER_METHOD)
          .addModifiers(KModifier.OVERRIDE)
          .addCode("""
            |return mapOf(
            |  ${routes.toList().joinToString { (r, f) -> "$r::class to $f::class" }}
            |)
          """.trimMargin())
          .returns(RouterEnv.routeMapName)
          .build()
      )
      .build()
    fileBuilder
      .addType(registrar)
      .build()
      .writeTo(dir)
  }
}
