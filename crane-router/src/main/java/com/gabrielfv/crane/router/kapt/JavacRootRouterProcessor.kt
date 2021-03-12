package com.gabrielfv.crane.router.kapt

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.KAPT_KOTLIN_GENERATED
import com.gabrielfv.crane.router.RouterEnv
import com.gabrielfv.crane.router.e
import com.gabrielfv.crane.router.generating.FileBuilder
import com.gabrielfv.crane.router.generating.RouterBuilder
import com.gabrielfv.crane.router.kaptGeneratedSourcesDir
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


/**
 * This will become an XStep if we're able to use indirection with Room Processing and KSP.
 * https://github.com/google/ksp/issues/344
 */
@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class JavacRootRouterProcessor : AbstractProcessor() {
  private val builder: RouterBuilder = RouterBuilder()
  private val elementUtils get() = processingEnv.elementUtils
  private val messenger get() = processingEnv.messager
  private var deferredRoot = ""

  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return mutableSetOf(
      CraneRoot::class.java.name,
      RouteRegistrar::class.java.name
    )
  }

  override fun getSupportedOptions(): MutableSet<String> {
    return mutableSetOf(
      KAPT_KOTLIN_GENERATED,
      ISOLATING.processorOption
    )
  }

  override fun process(
    annotations: MutableSet<out TypeElement>,
    roundEnv: RoundEnvironment
  ): Boolean {
    val registrars = fetchRegistrars()
      .map { it.simpleName.toString() }
      .toMutableList()
    val rootPackage = fetchRootPackage(roundEnv)
    if (rootPackage.isNotBlank() && registrars.isNotEmpty()) {
      deferredRoot = ""
      buildRouterFile(registrars.toSet(), rootPackage)
    }
    return false
  }

  private fun fetchRootPackage(roundEnv: RoundEnvironment): String {
    val root = roundEnv.getElementsAnnotatedWith(CraneRoot::class.java)
    if (root.isEmpty()) return deferredRoot
    else if (root.size > 1) {
      messenger.e("Multiple ${CraneRoot::class.java.simpleName} found")
    }
    return elementUtils.getPackageOf(root.first())
      .qualifiedName
      .toString()
      .also { deferredRoot = it }
  }

  private fun fetchRegistrars(): Set<Element> =
    processingEnv.elementUtils
      .getPackageElement(RouterEnv.REGISTRARS_PACKAGE)
      ?.let { pkg ->
        pkg.enclosedElements
          .filter { it.getAnnotation(RouteRegistrar::class.java) != null }
      }?.toSet() ?: emptySet()

  private fun buildRouterFile(names: Set<String>, rootPackage: String) {
    if (names.isEmpty()) return
    if (rootPackage.isBlank()) return
    val file = FileBuilder.srcDir(processingEnv.kaptGeneratedSourcesDir)
    builder.build(file, names, rootPackage)
  }
}
