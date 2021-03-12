package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isTypeElement
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
import javax.tools.Diagnostic
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

internal class RoutingStep(
  private val registrarBuilder: RouteRegistrarBuilder
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
    val routes = elements
      ?.filter(::isFragmentDeclaration)
      ?.map(::routeFor)
      ?.toMap() ?: emptyMap()
    buildRegistrar(registrarBuilder, env.filer, routes)
    return emptySet()
  }

  private fun routeFor(element: XTypeElement): Pair<String, String> {
    val route = element.toAnnotationBox(RoutedBy::class)
      ?.getAsType(ANNOTATION_VALUE_METHOD)
      ?.typeName
      ?.toString()
    val target = element.className.canonicalName()
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
      "${RouterEnv.registrarInterfaceName.simpleName}_${routes.hashCode().absoluteValue}"
    builder.build(filer, className, routes)
  }
}
