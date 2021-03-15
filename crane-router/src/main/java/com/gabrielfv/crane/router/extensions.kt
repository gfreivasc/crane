package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMessager
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XProcessingStep
import androidx.room.compiler.processing.XTypeElement
import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.annotations.VisibleForTesting
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.lang.model.element.Element
import javax.tools.Diagnostic

const val KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"

fun <T> Collection<T>.contains(predicate: (T) -> Boolean): Boolean {
  return find(predicate) != null
}

fun Messager.e(message: String, element: Element? = null) {
  if (element != null) {
    printMessage(Diagnostic.Kind.ERROR, message, element)
  } else {
    printMessage(Diagnostic.Kind.ERROR, message)
  }
}

fun XMessager.e(message: String, element: XElement? = null) {
  printMessage(Diagnostic.Kind.ERROR, message, element)
}

fun XMessager.check(condition: Boolean, message: () -> String) {
  check(condition, null, message)
}

fun XMessager.check(condition: Boolean, element: XElement?, message: () -> String) {
  if (!condition) {
    e(message(), element)
  }
}

val ProcessingEnvironment.kaptGeneratedSourcesDir: String get() =
  options[KAPT_KOTLIN_GENERATED] ?: run {
    messager.e("Kotlin sources dir not found")
    ""
  }

// Room Processing currently does not support KSP element deferring
fun XProcessingStep.executeInKsp(
  env: XProcessingEnv,
  resolver: Resolver
): List<KSAnnotated> {
  check(env.backend == XProcessingEnv.Backend.KSP)
  val elementMap = mutableMapOf<XTypeElement, KSAnnotated>()
  val args = annotations().associateWith { annotation ->
    val elements = resolver.getSymbolsWithAnnotation(
      annotation.java.canonicalName
    ).filterIsInstance<KSClassDeclaration>()
      .map {
        val element = env.requireTypeElement(it.qualifiedName!!.asString())
        elementMap[element] = it
        element
      }
    elements
  }
  return process(env, args)
    .mapNotNull { elementMap[it] }
}

/**
 * Trying to hack our way through to get some test utilities to be
 * visible from our android-module tests.
 */
@VisibleForTesting
val List<BasicAnnotationProcessor>
  .asProcessorList: List<Processor> get() = this
