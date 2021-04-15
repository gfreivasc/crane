package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.generating.RouterBuilder

internal class RouterWiringStep(
  private val registrarFetcher: RegistrarFetcher,
  private val routerBuilder: RouterBuilder
) : XProcessingStep {

  override fun annotations(): Set<String> {
    return setOf(
      RouteRegistrar::class.qName,
      CraneRoot::class.qName
    )
  }

  override fun process(
    env: XProcessingEnv,
    elementsByAnnotation: Map<String, Set<XElement>>
  ): Set<XElement> {
    val rootAnnotated = elementsByAnnotation[CraneRoot::class.qName] ?: emptyList()
    val localRegistrars = elementsByAnnotation[RouteRegistrar::class.qName]
      ?.filter { it.isRouteRegistrar }
      ?.map { it as XTypeElement }
      ?.toSet() ?: emptySet()
    val registrars = registrarFetcher.fetch(env, localRegistrars)
    val root = fetchRoot(env, rootAnnotated) ?: return emptySet()
    if (registrars.isEmpty()) return setOf(root)
    buildRouter(env, root, registrars)
    return emptySet()
  }

  private fun fetchRoot(env: XProcessingEnv, annotated: Collection<XElement>): XTypeElement? {
    if (annotated.size > 1) env.messager.e(
      "Multiple ${CraneRoot::class.java.simpleName} instances found"
    )
    return annotated.firstOrNull()
      ?.let { it as? XTypeElement }
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

  private val XElement.isRouteRegistrar: Boolean get() =
    this is XTypeElement && getSuperInterfaceElements().any {
      it.qualifiedName == RouterEnv.registrarInterfaceName.toString()
    }
}
