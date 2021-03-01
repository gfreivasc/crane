package com.gabrielfv.crane.router

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.impl.MessageCollectorBasedKSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

@AutoService(SymbolProcessor::class)
class RouterProcessor : SymbolProcessor {
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger
  private val generator = RouterGenerator()

  companion object {
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
    val symbols = resolver.getSymbolsWithAnnotation(CraneRoot::class.java.name)
    if (symbols.isEmpty()) return emptyList()
    val rootPackage = symbols.first()
      .let { it as KSClassDeclaration }
      .packageName
      .asString()
    val registrars = resolver
      .getSymbolsWithAnnotation(RouteRegistrar::class.java.name)
      .filterIsInstance<KSClassDeclaration>()
      .filter { it.isInRegistrarPackage }
    if (registrars.isNotEmpty()) {
      processRegistrars(registrars, rootPackage)
      return emptyList()
    }
    return symbols
  }

  private fun processRegistrars(registrars: List<KSClassDeclaration>, rootPackage: String) {
    if (registrars.isEmpty()) return
    val file = codeGenerator.createNewFile(
      Dependencies(
        aggregating = false,
        *registrars.map { it.containingFile!! }.toTypedArray()
      ),
      rootPackage,
      CLASS_NAME
    )
    val names = registrars.map { it.simpleName.asString() }
    generator.generate(file, names, rootPackage)
  }

  override fun onError() {
    (logger as? MessageCollectorBasedKSPLogger)?.reportAll()
    throw RuntimeException("Check KSP messages for errors")
  }

  private val KSClassDeclaration.isInRegistrarPackage: Boolean
    get() = packageName.asString() == RouteRegistrarGenerator.PACKAGE_NAME
}
