package com.gabrielfv.crane.router.kapt

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.KAPT_KOTLIN_GENERATED
import com.gabrielfv.crane.router.RouterEnv
import com.gabrielfv.crane.router.e
import com.gabrielfv.crane.router.generating.FileBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.gabrielfv.crane.router.kaptGeneratedSourcesDir
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSetMultimap
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element


/**
 * This will be removed in favor of
 * [com.gabrielfv.crane.router.RouterWiringStep] when we can somehow
 * resolve dependency files in a strategy similar to the one employed
 * here with [javax.lang.model.util.Elements.getPackageElement], which
 * currently isn't available in KSP also.
 */
@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class JavacRouterWiringProcessor : BasicAnnotationProcessor() {
  private val builder: RouterBuilder = RouterBuilder()

  override fun steps(): MutableIterable<BasicAnnotationProcessor.Step> {
    return mutableSetOf(Step(processingEnv, builder))
  }

  override fun getSupportedOptions(): MutableSet<String> {
    return mutableSetOf(
      KAPT_KOTLIN_GENERATED,
      ISOLATING.processorOption
    )
  }

  internal class Step(
    private val processingEnv: ProcessingEnvironment,
    private val builder: RouterBuilder
  ) : BasicAnnotationProcessor.Step {

    override fun annotations(): MutableSet<String> {
      return mutableSetOf(
        CraneRoot::class.java.name,
        RouteRegistrar::class.java.name
      )
    }

    override fun process(
      elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): MutableSet<out Element> {
      val registrars = fetchRegistrars()
        .map { it.simpleName.toString() }
        .toSet()
      val rootPackage = fetchRootPackage(elementsByAnnotation)
      return if (rootPackage != null) {
        if (registrars.isNotEmpty()) {
          buildRouterFile(registrars, rootPackage.second)
          mutableSetOf()
        } else {
          mutableSetOf(rootPackage.first)
        }
      } else {
        mutableSetOf()
      }
    }

    private fun fetchRegistrars(): Set<Element> =
      processingEnv.elementUtils
        .getPackageElement(RouterEnv.REGISTRARS_PACKAGE)
        ?.enclosedElements
        ?.filter { it.getAnnotation(RouteRegistrar::class.java) != null }
        ?.toSet() ?: emptySet()

    private fun fetchRootPackage(
      elementsByAnnotation: ImmutableSetMultimap<String, Element>
    ): Pair<Element, String>? {
      val roots = elementsByAnnotation[CraneRoot::class.java.name]
      if (roots.size > 1) {
        processingEnv.messager.e("Multiple ${CraneRoot::class.java.simpleName} found")
      }
      return roots.firstOrNull()
        ?.let { root ->
          root to processingEnv.elementUtils
            .getPackageOf(root)
            .qualifiedName
            .toString()
        }
    }

    private fun buildRouterFile(names: Set<String>, rootPackage: String) {
      if (names.isEmpty()) return
      if (rootPackage.isBlank()) return
      val file = FileBuilder.srcDir(processingEnv.kaptGeneratedSourcesDir)
      builder.build(file, names, rootPackage)
    }
  }
}
