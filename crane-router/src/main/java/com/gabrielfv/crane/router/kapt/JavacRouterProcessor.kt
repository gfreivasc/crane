package com.gabrielfv.crane.router.kapt

import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor
import com.gabrielfv.crane.router.KAPT_KOTLIN_GENERATED
import com.gabrielfv.crane.router.RouterWiringStep
import com.gabrielfv.crane.router.RoutingStep
import com.gabrielfv.crane.router.generating.JavaRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class JavacRouterProcessor : JavacBasicAnnotationProcessor() {
  private val routeRegistrarBuilder: RouteRegistrarBuilder =
    JavaRouteRegistrarBuilder()
  private val routerBuilder: RouterBuilder = RouterBuilder()

  override fun processingSteps(): Iterable<XProcessingStep> {
    return setOf(
      RoutingStep(routeRegistrarBuilder),
      RouterWiringStep(routerBuilder)
    )
  }

  override fun getSupportedOptions(): MutableSet<String> {
    return mutableSetOf(
      KAPT_KOTLIN_GENERATED,
      ISOLATING.processorOption
    )
  }
}
