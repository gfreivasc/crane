package com.gabrielfv.crane.router

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.impl.MessageCollectorBasedKSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue

@AutoService(SymbolProcessor::class)
class RouteRegistrarProcessor : SymbolProcessor {
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger

  companion object {
    const val ANNOTATION_FQ_NAME = "com.gabrielfv.crane.router.RoutedBy"
    const val FRAGMENT_FQ_NAME = "androidx.fragment.app.Fragment"
    const val PACKAGE_NAME = "com.gabrielfv.crane.routes.registrar"
    const val CLASS_NAME = "RouteRegistrar"
    const val ROUTE_MAP_FQ_NAME = "com.gabrielfv.crane.core.RouteMap"
  }

  override fun init(
    options: Map<String, String>,
    kotlinVersion: KotlinVersion,
    codeGenerator: CodeGenerator,
    logger: KSPLogger
  ) {
    this.codeGenerator = codeGenerator
    this.logger = logger
  }

  override fun finish() {}

  override fun process(resolver: Resolver): List<KSAnnotated> {
    val symbols = resolver.getSymbolsWithAnnotation(ANNOTATION_FQ_NAME)
    val input = symbols.filterIsInstance<KSClassDeclaration>()
    if (input.isNotEmpty()) {
      val output = mutableMapOf<String, String>()
      val visitor = RouteRegistrarVisitor(output, logger)
      input.forEach { it.accept(visitor, Unit) }
      val originatingFiles = input.map { it.containingFile!! }
      buildFile(output, originatingFiles)
    }
    return symbols.filterNot { it is KSClassDeclaration }
  }

  private fun buildFile(routes: MutableMap<String, String>, originatingFiles: List<KSFile>) {
    if (routes.isEmpty()) return
    val className = "${CLASS_NAME}_${originatingFiles.hashCode().absoluteValue}"
    val file = codeGenerator.createNewFile(
      Dependencies(
        aggregating = false,
        *originatingFiles.toTypedArray()
      ),
      PACKAGE_NAME,
      className
    )
    OutputStreamWriter(file, StandardCharsets.UTF_8)
      .use { writer ->
        writer.append("package $PACKAGE_NAME\n\n")
        writer.append("import $ROUTE_MAP_FQ_NAME\n\n")
        writer.append("object $className {\n")
        writer.append("  fun get(): RouteMap = mapOf(\n")
        val formattedRoutes = routes.map { (routeFqName, targetFqName) ->
          "    $routeFqName::class to $targetFqName::class"
        }.joinToString(",\n")
        writer.append("$formattedRoutes\n")
        writer.append("  )\n")
        writer.append("}\n")
      }
  }

  override fun onError() {
    (logger as? MessageCollectorBasedKSPLogger)?.reportAll()
    throw RuntimeException("Check KSP messages for errors")
  }
}
