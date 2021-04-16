package com.gabrielfv.crane.router.ksp

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.RouterEnv
import com.gabrielfv.crane.router.contains
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.gabrielfv.crane.router.qName
import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import kotlin.reflect.KClass

@KspExperimental
@AutoService(SymbolProcessor::class)
class KspRouterWiringProcessor : SymbolProcessor {
  private val builder: RouterBuilder = RouterBuilder()
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger
  private lateinit var resolver: Resolver

  private val supportedAnnotations: Set<KClass<out Annotation>> = setOf(
    CraneRoot::class
  )

  override fun init(
    options: Map<String, String>,
    kotlinVersion: KotlinVersion,
    codeGenerator: CodeGenerator,
    logger: KSPLogger
  ) {
    this.codeGenerator = codeGenerator
    this.logger = CraneKSPLogger(logger)
  }

  override fun process(resolver: Resolver): List<KSAnnotated> {
    this.resolver = resolver
    val elements = getElements(resolver)
    return if (elements.isNotEmpty()) {
      process(getElements(resolver))
    } else emptyList()
  }

  private fun getElements(resolver: Resolver): Map<String, Set<KSAnnotated>> =
    supportedAnnotations
      .map { it.qName }
      .associateWith { name ->
        resolver.getSymbolsWithAnnotation(name)
          .toSet()
      }

  private fun process(annotated: Map<String, Set<KSAnnotated>>): List<KSAnnotated> {
    val registrars = fetchRegistrars()
    val root = fetchRoot(annotated[CraneRoot::class.qName]!!)
    return if (root != null) {
      val (rootAnnotated, rootPackage) = root
      if (registrars.isNotEmpty()) {
        buildRouterFile(registrars, rootPackage, rootAnnotated as KSDeclaration)
        emptyList()
      } else {
        listOf(rootAnnotated)
      }
    } else {
      emptyList()
    }
  }

  private fun fetchRoot(
    rootElements: Set<KSAnnotated>
  ): Pair<KSAnnotated, String>? {
    if (rootElements.size > 1) {
      logger.error("Multiple @${CraneRoot::class.simpleName} instances found")
    }
    return rootElements.firstOrNull()
      ?.let { element ->
        element to element
          .let { it as KSDeclaration }
          .packageName
          .asString()
      }
  }

  private fun fetchRegistrars(): Set<KSClassDeclaration> {
    val global = resolver
      .getDeclarationsFromPackage(RouterEnv.REGISTRARS_PACKAGE)
      .filterIsInstance<KSClassDeclaration>()
      .toSet()
    return global
      .filter(::isRegistrarAnnotated)
      .toSet()
  }

  private fun isRegistrarAnnotated(declaration: KSClassDeclaration): Boolean {
    return declaration.annotations.contains {
      it.annotationType
        .resolve()
        .declaration
        .qualifiedName!!
        .asString() == RouteRegistrar::class.qName
    }
  }

  private fun buildRouterFile(
    registrars: Set<KSClassDeclaration>,
    rootPackage: String,
    originating: KSDeclaration
  ) {
    if (registrars.isEmpty()) return
    if (rootPackage.isBlank()) return
    val dependencies = registrars
      .map {
        it.containingFile!!
      } + originating.containingFile!!
    val file = codeGenerator.createNewFile(
      Dependencies(
        aggregating = true,
        sources = dependencies.toTypedArray()
      ),
      rootPackage,
      fileName = RouterEnv.ROUTER_CLASS
    )
    val names = registrars
      .map { it.simpleName.asString() }
      .toSet()
    builder.build(file, names, rootPackage)
  }
}
