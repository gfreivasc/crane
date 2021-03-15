package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.generating.RouterBuilder
import kotlin.reflect.KClass

internal class RouterWiringStep(
  private val registrarFetcher: RegistrarFetcher,
  private val routerBuilder: RouterBuilder
) : XProcessingStep {

  override fun annotations(): Set<KClass<out Annotation>> {
    return setOf(
      RouteRegistrar::class,
      CraneRoot::class
    )
  }

  override fun process(
    env: XProcessingEnv,
    elementsByAnnotation: Map<KClass<out Annotation>, List<XTypeElement>>
  ): Set<XTypeElement> {
    val rootAnnotated = elementsByAnnotation[CraneRoot::class] ?: emptyList()
    val localRegistrars = elementsByAnnotation[RouteRegistrar::class]
      ?.filter { it.isRouteRegistrar }
      ?.toSet() ?: emptySet()
    val registrars = registrarFetcher.fetch(env, localRegistrars)
    val root = fetchRoot(env, rootAnnotated) ?: return emptySet()
    if (registrars.isEmpty()) return setOf(root)
    buildRouter(env, root, registrars)
    return emptySet()
  }

  private fun fetchRoot(env: XProcessingEnv, annotated: List<XTypeElement>): XTypeElement? {
    if (annotated.size > 1) env.messager.e(
      "Multiple ${CraneRoot::class.java.simpleName} found"
    )
    return annotated.firstOrNull()
  }

  private fun buildRouter(
    env: XProcessingEnv,
    root: XTypeElement,
    registrars: Set<XTypeElement>
  ) {
    val pkg = root.packageName
    val registrarNames = registrars
      .map { it.className.simpleName() }
      .toSet()
    routerBuilder.build(env.filer, registrarNames, pkg)
  }

  private val XTypeElement.isRouteRegistrar: Boolean get() =
    getSuperInterfaceElements().any {
      it.qualifiedName == RouterEnv.registrarInterfaceName.toString()
    }
}
