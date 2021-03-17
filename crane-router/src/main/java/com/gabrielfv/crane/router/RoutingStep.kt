package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isTypeElement
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

internal class RoutingStep(
  private val registrarBuilder: RouteRegistrarBuilder,
) : XProcessingStep {

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
      ?.filter { element ->
        val valid = isFragmentDeclaration(element)
        env.messager.check(valid, element) {
          "@RoutedBy should only be used against " +
            "${RouterEnv.fragmentName.canonicalName} instances"
        }
        valid
      } ?: emptyList()
    val (originating, routes) = mapRoutes(env, elements)
    buildRegistrar(registrarBuilder, env.filer, routes, originating.toSet())
    return emptySet()
  }

  private fun mapRoutes(
    env: XProcessingEnv,
    elements: List<XTypeElement>
  ): Pair<List<XTypeElement>, Map<String, String>> {
    val mutableRoutes = mutableMapOf<String, String>()
    val originatingElements = mutableListOf<XTypeElement>()
    elements.forEach { element ->
      val (originating, route) = routeFor(element)
      env.messager.check(!mutableRoutes.containsKey(route.first), element) {
        "Route ${route.first} is routing multiple different fragments."
      }
      originatingElements.add(originating)
      mutableRoutes[route.first] = route.second
    }
    return originatingElements to mutableRoutes
  }

  private fun routeFor(element: XTypeElement): ElementRoute {
    val route = element.toAnnotationBox(RoutedBy::class)
      ?.getAsType(ANNOTATION_VALUE_METHOD)
      ?.typeName
      ?.toString() ?: "<ERROR>"
    val target = element.className.canonicalName()
    return ElementRoute(element, route to target)
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
    routes: Map<String, String>,
    originating: Set<XTypeElement>
  ) {
    if (routes.isEmpty()) return
    val className =
      "${RouterEnv.registrarInterfaceName.simpleName}_${routes.hashCode().absoluteValue}"
    builder.build(filer, className, routes, originating)
  }

  private data class ElementRoute(
    val originating: XTypeElement,
    val route: Pair<String, String>
  )
}
