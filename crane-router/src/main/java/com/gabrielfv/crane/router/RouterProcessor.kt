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
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue

@AutoService(SymbolProcessor::class)
class RouterProcessor : SymbolProcessor {
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger

  companion object {
    private const val ROOT_ANNOTATION_FQ_NAME = "com.gabrielfv.crane.router.CraneRoot"
    private const val PACKAGE_NAME = "com.gabrielfv.crane.routes"
    private const val CLASS_NAME = "Router"
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
    val symbols = resolver.getSymbolsWithAnnotation(ROOT_ANNOTATION_FQ_NAME)
    if (symbols.isEmpty()) return emptyList()
    val registrars = resolver
      .getSymbolsWithAnnotation(RouteRegistrarProcessor.REG_ANNOTATION_FQ_NAME)
      .filterIsInstance<KSClassDeclaration>()
    if (registrars.isNotEmpty()) {
      processRegistrars(registrars)
      return emptyList()
    }
    return symbols
  }

  private fun processRegistrars(registrars: List<KSClassDeclaration>) {
    if (registrars.isEmpty()) return
    val file = codeGenerator.createNewFile(
      Dependencies(
        aggregating = false,
        *registrars.map { it.containingFile!! }.toTypedArray()
      ),
      PACKAGE_NAME,
      CLASS_NAME
    )
    OutputStreamWriter(file, StandardCharsets.UTF_8)
      .use { writeFile(it, registrars) }
  }

  private fun writeFile(writer: OutputStreamWriter, registrars: List<KSClassDeclaration>) {
    writer.append("package $PACKAGE_NAME\n\n")
    writer.append("import ${RouteRegistrarProcessor.PACKAGE_NAME}.*\n\n")
    writer.append("internal object $CLASS_NAME {\n")
    writer.append("  private val registrars get() = setOf(\n")
    val declaration = registrars.joinToString(",\n") {
      "    ${it.simpleName.asString()}"
    }
    writer.append("$declaration\n")
    writer.append("  )\n\n")
    writer.append("  fun get() = registrars.flatMap { registrar ->\n")
    writer.append("    registrar.get().toList()\n")
    writer.append("  }.toMap()\n")
    writer.append("}\n")
  }

  override fun onError() {
    (logger as? MessageCollectorBasedKSPLogger)?.reportAll()
    throw RuntimeException("Check KSP messages for errors")
  }

  private val KSClassDeclaration.isInRegistrarPackage: Boolean
    get() = packageName.asString() == RouteRegistrarProcessor.PACKAGE_NAME

}
