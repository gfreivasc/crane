package com.gabrielfv.crane.router.kapt

import com.gabrielfv.crane.router.KAPT_KOTLIN_GENERATED
import com.gabrielfv.crane.router.RoutingStep
import com.gabrielfv.crane.router.generating.JavaRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class JavacRouterProcessor : BasicAnnotationProcessor() {
  private val routeRegistrarBuilder: RouteRegistrarBuilder =
    JavaRouteRegistrarBuilder()

  override fun initSteps(): MutableIterable<ProcessingStep> {
    return mutableSetOf(
      RoutingStep(routeRegistrarBuilder)
        .asAutoCommonProcessor(processingEnv)
    )
  }

  override fun getSupportedOptions(): MutableSet<String> {
    return mutableSetOf(
      KAPT_KOTLIN_GENERATED,
      ISOLATING.processorOption
    )
  }
}
