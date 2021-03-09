package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isTypeElement
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.router.generating.JavaRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

class RoutingStep : XProcessingStep {

  companion object {
    const val ANNOTATION_VALUE_METHOD = "value"
  }

  override fun annotations(): Set<KClass<out Annotation>> {
    return mutableSetOf(RoutedBy::class)
  }

  override fun process(
    env: XProcessingEnv,
    elementsByAnnotation: Map<KClass<out Annotation>, List<XTypeElement>>
  ): Set<XTypeElement> {
    val elements = elementsByAnnotation[RoutedBy::class]
    val routes = elements
      ?.filter(::isFragmentDeclaration)
      ?.map(::routeFor)
      ?.toMap() ?: emptyMap()
    buildRegistrar(env.registrarBuilder, env.filer, routes)
    return emptySet()
  }

  private fun routeFor(element: XTypeElement): Pair<String, String> {
    val route = element.toAnnotationBox(RoutedBy::class)
      ?.getAsType(ANNOTATION_VALUE_METHOD)
      ?.typeName
      ?.toString()
    val target = element.qualifiedName
    return route!! to target
  }

  private fun isFragmentDeclaration(element: XTypeElement): Boolean {
    var sup = element.superType
    while (sup != null) {
      if (sup.typeElement?.qualifiedName == RouterEnv.fragmentName.toString()) {
        return element.isTypeElement()
      }
      sup = sup.typeElement?.superType
    }
    return false
  }

  private fun buildRegistrar(
    builder: RouteRegistrarBuilder,
    filer: XFiler,
    routes: Map<String, String>
  ) {
    if (routes.isEmpty()) return
    val className =
      "${RouteRegistrarGenerator.CLASS_NAME}_${routes.hashCode().absoluteValue}"
    builder.build(filer, className, routes)
  }

  private val XProcessingEnv.registrarBuilder: RouteRegistrarBuilder get() =
    when (backend) {
      XProcessingEnv.Backend.JAVAC -> JavaRouteRegistrarBuilder()
      /* TODO: replace with KtRouteRegistrarBuilder
       * Depends on: https://issuetracker.google.com/issues/182195680
       */
      XProcessingEnv.Backend.KSP -> JavaRouteRegistrarBuilder()
    }
}
