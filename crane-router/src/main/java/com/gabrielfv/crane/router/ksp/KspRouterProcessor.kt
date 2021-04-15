package com.gabrielfv.crane.router.ksp

import androidx.room.compiler.processing.XProcessingEnv
import com.gabrielfv.crane.router.ConfinedRegistrarFetcher
import com.gabrielfv.crane.router.RegistrarFetcher
import com.gabrielfv.crane.router.RouterWiringStep
import com.gabrielfv.crane.router.RoutingStep
import com.gabrielfv.crane.router.generating.KtRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

@AutoService(SymbolProcessor::class)
class KspRouterProcessor : SymbolProcessor {
  private val routeRegistrarBuilder: RouteRegistrarBuilder = KtRouteRegistrarBuilder()
  private val routerBuilder: RouterBuilder = RouterBuilder()
  // There's currently no API for indirection fetching in KSP
  // See https://github.com/google/ksp/issues/344
  private val registrarFetcher: RegistrarFetcher = ConfinedRegistrarFetcher()
  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: CraneKSPLogger

  override fun finish() {
    logger.reportErrors()
  }

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
    val processingEnv = XProcessingEnv.create(
      emptyMap(),
      resolver,
      codeGenerator,
      logger
    )

    RoutingStep(routeRegistrarBuilder)
      .executeInKsp(processingEnv)
    return RouterWiringStep(registrarFetcher, routerBuilder)
      .executeInKsp(processingEnv)
  }
}
