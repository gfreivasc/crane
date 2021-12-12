package com.gabrielfv.crane.router.ksp

import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.ksp.KspBasicAnnotationProcessor
import com.gabrielfv.crane.router.ConfinedRegistrarFetcher
import com.gabrielfv.crane.router.RegistrarFetcher
import com.gabrielfv.crane.router.RouterWiringStep
import com.gabrielfv.crane.router.RoutingStep
import com.gabrielfv.crane.router.generating.KtRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KspRouterProcessor(environment: SymbolProcessorEnvironment) : KspBasicAnnotationProcessor(environment) {
  private val routeRegistrarBuilder: RouteRegistrarBuilder = KtRouteRegistrarBuilder()
  private val routerBuilder: RouterBuilder = RouterBuilder()
  // There's currently no API for indirection fetching in KSP
  // See https://github.com/google/ksp/issues/344
  private val registrarFetcher: RegistrarFetcher = ConfinedRegistrarFetcher()

  override fun processingSteps(): Iterable<XProcessingStep> {
    return setOf(
      RoutingStep(routeRegistrarBuilder),
      RouterWiringStep(registrarFetcher, routerBuilder)
    )
  }

  @AutoService(SymbolProcessorProvider::class)
  class Provider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
      KspRouterProcessor(environment)
  }
}
