package com.gabrielfv.crane.router.ksp

import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.ksp.KspBasicAnnotationProcessor
import com.gabrielfv.crane.router.RouterWiringStep
import com.gabrielfv.crane.router.RoutingStep
import com.gabrielfv.crane.router.generating.KtRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING

class KspRouterProcessor(environment: SymbolProcessorEnvironment) : KspBasicAnnotationProcessor(environment) {
  private val routeRegistrarBuilder: RouteRegistrarBuilder = KtRouteRegistrarBuilder()
  private val routerBuilder: RouterBuilder = RouterBuilder()

  override fun processingSteps(): Iterable<XProcessingStep> {
    return setOf(
      RoutingStep(routeRegistrarBuilder),
      RouterWiringStep(routerBuilder)
    )
  }

  @AutoService(SymbolProcessorProvider::class)
  @IncrementalAnnotationProcessor(ISOLATING)
  class Provider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
      KspRouterProcessor(environment)
  }
}
