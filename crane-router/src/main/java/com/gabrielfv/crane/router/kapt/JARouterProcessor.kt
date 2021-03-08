package com.gabrielfv.crane.router.kapt

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.KAPT_KOTLIN_GENERATED
import com.gabrielfv.crane.router.RouteRegistrarGenerator
import com.gabrielfv.crane.router.contains
import com.gabrielfv.crane.router.e
import com.gabrielfv.crane.router.generating.FileBuilder
import com.gabrielfv.crane.router.generating.JavaRouteRegistrarBuilder
import com.gabrielfv.crane.router.generating.RouteRegistrarBuilder
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
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import kotlin.math.absoluteValue

@IncrementalAnnotationProcessor(ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class JARouterProcessor : AbstractProcessor() {
  private val routerBuilder: RouterBuilder = RouterBuilder()
  private val routeRegistrarBuilder: RouteRegistrarBuilder = JavaRouteRegistrarBuilder()
  private val elementUtils get() = processingEnv.elementUtils
  private val typeUtils get() = processingEnv.typeUtils
  private val messenger get() = processingEnv.messager
  private var deferredRoot = ""

  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return mutableSetOf(
      CraneRoot::class.java.name,
      RoutedBy::class.java.name,
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
    processRoutes(roundEnv)
    processRouter(roundEnv)
    return false
  }

  private fun processRoutes(roundEnv: RoundEnvironment) {
    val elements = roundEnv.getElementsAnnotatedWith(RoutedBy::class.java)
    val routes = elements
      .filter(::isFragmentDeclaration)
      .map(::mapRoute)
      .toMap()
    buildRegistrarFile(routes)
  }

  private fun processRouter(roundEnv: RoundEnvironment) {
    val registrars = fetchRegistrars()
      .map { it.simpleName.toString() }
      .toMutableList()
    val rootPackage = fetchRootPackage(roundEnv)
    if (rootPackage.isNotBlank() && registrars.isNotEmpty()) {
      deferredRoot = ""
      buildRouterFile(registrars.toSet(), rootPackage)
    }
  }

  private fun mapRoute(element: Element): Pair<String, String> {
    val route = try {
      element.getAnnotation(RoutedBy::class.java)
        .value
        .java.name
    } catch (ex: MirroredTypeException) {
      val annotationElement = ex.typeMirror
        .let { (it as DeclaredType).asElement() }
      val annotationPackage = processingEnv.elementUtils
        .getPackageOf(annotationElement)
        .qualifiedName
      "$annotationPackage.${annotationElement.simpleName}"
    }
    val targetPackage = processingEnv
      .elementUtils
      .getPackageOf(element)
      .qualifiedName
    val target = "$targetPackage.${element.simpleName}"
    return route to target
  }

  private fun fetchRootPackage(roundEnv: RoundEnvironment): String {
    val root = roundEnv.getElementsAnnotatedWith(CraneRoot::class.java)
    if (root.isEmpty()) return deferredRoot
    else if (root.size > 1) messenger.e("Multiple ${CraneRoot::class.java.simpleName} found")
    return elementUtils.getPackageOf(root.first())
      .qualifiedName
      .toString()
      .also { deferredRoot = it }
  }

  private fun fetchRegistrars(): Set<Element> =
    processingEnv.elementUtils
      .getPackageElement(RouteRegistrarGenerator.PACKAGE_NAME)
      ?.let { pkg ->
        pkg.enclosedElements
          .filter { it.getAnnotation(RouteRegistrar::class.java) != null }
      }?.toSet() ?: emptySet()

  private fun buildRegistrarFile(routes: Map<String, String>): String? {
    if (routes.isEmpty()) return null
    val className =
      "${RouteRegistrarGenerator.CLASS_NAME}_${routes.hashCode().absoluteValue}"
    routeRegistrarBuilder.build(processingEnv.filer, className, routes)
    return className
  }

  private fun buildRouterFile(names: Set<String>, rootPackage: String) {
    if (names.isEmpty()) return
    if (rootPackage.isBlank()) return
    val file = FileBuilder.srcDir(processingEnv.kaptGeneratedSourcesDir)
    routerBuilder.build(file, names, rootPackage)
  }

  private fun isFragmentDeclaration(element: Element): Boolean {
    return element.kind == ElementKind.CLASS &&
      typeUtils.directSupertypes(element.asType())
        .map { (it as DeclaredType).asElement() }
        .contains { it.simpleName.toString() == "Fragment" }
  }
}
