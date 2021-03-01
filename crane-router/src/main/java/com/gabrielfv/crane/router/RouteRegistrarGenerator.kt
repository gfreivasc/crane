package com.gabrielfv.crane.router

import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class RouteRegistrarGenerator {

  companion object {
    const val PACKAGE_NAME = "com.gabrielfv.crane.routes.registrar"
    const val CLASS_NAME = "RouteRegistrar"
    private const val REGISTRAR_INTERFACE_PACKAGE = "com.gabrielfv.crane.router"
    private const val REGISTRAR_INTERFACE = "RouteRegistrar"
  }

  fun generate(file: OutputStream, className: String, routes: Map<String, String>) {
    OutputStreamWriter(file, StandardCharsets.UTF_8)
      .use { writer ->
        writer.append("package ${PACKAGE_NAME}\n\n")
        writer.append("import $REGISTRAR_INTERFACE_PACKAGE.$REGISTRAR_INTERFACE\n")
        writer.append("import ${Types.ROUTE_MAP}\n\n")
        writer.append("@${RouteRegistrar::class.java.name}\n")
        writer.append("object $className : $REGISTRAR_INTERFACE {\n")
        writer.append("  override fun get(): RouteMap = mapOf(\n")
        val formattedRoutes = routes.map { (routeFqName, targetFqName) ->
          "    $routeFqName::class to $targetFqName::class"
        }.joinToString(",\n")
        writer.append("$formattedRoutes\n")
        writer.append("  )\n")
        writer.append("}\n")
      }
  }
}
