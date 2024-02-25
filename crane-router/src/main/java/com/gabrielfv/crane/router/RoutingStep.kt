package com.gabrielfv.crane.router

import androidx.room.compiler.codegen.toJavaPoet
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isTypeElement
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import kotlin.math.absoluteValue

internal class RoutingStep(
  private val registrarBuilder: RouteRegistrarBuilder,
) : XProcessingStep {

  companion object {
    const val ANNOTATION_VALUE_METHOD = "value"
  }

  override fun annotations(): Set<String> {
    return mutableSetOf(RoutedBy::class.qName)
  }

  override fun process(
    env: XProcessingEnv,
    elementsByAnnotation: Map<String, Set<XElement>>,
    isLastRound: Boolean,
  ): Set<XTypeElement> {
    val elements = elementsByAnnotation[RoutedBy::class.qName]
      ?.filter { element ->
        val valid = isFragmentDeclaration(element)
        env.messager.check(valid, element) {
          "@RoutedBy should only be used against " +
            "${RouterEnv.fragmentName.canonicalName} instances"
        }
        valid
      }?.map { it as XTypeElement } ?: emptyList()
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
    val route = element.getAnnotation(RoutedBy::class)
      ?.getAsType(ANNOTATION_VALUE_METHOD)
      ?.typeName
      ?.toString() ?: "<ERROR>"
    val target = element.asClassName().toJavaPoet().canonicalName()
    return ElementRoute(element, route to target)
  }

  private fun isFragmentDeclaration(element: XElement): Boolean {
    if (element !is XTypeElement) return false
    var sup = element.superClass
    while (sup != null) {
      if (sup.typeElement?.qualifiedName == RouterEnv.fragmentName.toString()) {
        return element.isTypeElement()
      }
      sup = sup.typeElement?.superClass
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
