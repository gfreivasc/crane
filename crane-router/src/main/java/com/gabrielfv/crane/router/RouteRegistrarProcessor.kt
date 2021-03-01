package com.gabrielfv.crane.router

import com.gabrielfv.crane.annotations.RoutedBy
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
import kotlin.math.absoluteValue

@AutoService(SymbolProcessor::class)
class RouteRegistrarProcessor : SymbolProcessor {
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger
  private val generator = RouteRegistrarGenerator()

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
    val symbols = resolver.getSymbolsWithAnnotation(RoutedBy::class.java.name)
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
    val className =
      "${RouteRegistrarGenerator.CLASS_NAME}_${originatingFiles.hashCode().absoluteValue}"
    val file = codeGenerator.createNewFile(
      Dependencies(
        aggregating = false,
        *originatingFiles.toTypedArray()
      ),
      RouteRegistrarGenerator.PACKAGE_NAME,
      className
    )
    generator.generate(file, className, routes)
  }

  override fun onError() {
    (logger as? MessageCollectorBasedKSPLogger)?.reportAll()
    throw RuntimeException("Check KSP messages for errors")
  }
}
