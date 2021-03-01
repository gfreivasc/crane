package com.gabrielfv.crane.router

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class RouterGenerator {

  companion object {
    const val CLASS_NAME = "Router"
  }

  fun generate(
    file: OutputStream,
    registrars: List<String>,
    rootPackage: String
  ) {
    OutputStreamWriter(file, StandardCharsets.UTF_8)
      .use { writer ->
        writer.append("package $rootPackage\n\n")
        writer.append("import ${RouteRegistrarGenerator.PACKAGE_NAME}.*\n\n")
        writer.append("internal object $CLASS_NAME {\n")
        writer.append("  private val registrars get() = setOf(\n")
        val declaration = registrars.joinToString(",\n") { name ->
          "    $name"
        }
        writer.append("$declaration\n")
        writer.append("  )\n\n")
        writer.append("  fun get() = registrars.flatMap { registrar ->\n")
        writer.append("    registrar.get().toList()\n")
        writer.append("  }.toMap()\n")
        writer.append("}\n")
      }
  }
}
