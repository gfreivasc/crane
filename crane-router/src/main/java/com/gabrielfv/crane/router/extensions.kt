package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMessager
import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.annotations.VisibleForTesting
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.lang.model.element.Element
import javax.tools.Diagnostic
import kotlin.reflect.KClass

const val KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"

fun <T> Collection<T>.contains(predicate: (T) -> Boolean): Boolean {
  return find(predicate) != null
}

val KClass<*>.qName: String get() = java.name

fun Messager.e(message: String, element: Element? = null) {
  if (element != null) {
    printMessage(Diagnostic.Kind.ERROR, message, element)
  } else {
    printMessage(Diagnostic.Kind.ERROR, message)
  }
}

fun XMessager.e(message: String, element: XElement? = null) {
  if (element != null) {
    printMessage(Diagnostic.Kind.ERROR, message, element)
  } else {
    printMessage(Diagnostic.Kind.ERROR, message)
  }
}

fun XMessager.check(condition: Boolean, element: XElement, message: () -> String) {
  if (!condition) {
    e(message(), element)
  }
}

val ProcessingEnvironment.kaptGeneratedSourcesDir: String get() =
  options[KAPT_KOTLIN_GENERATED] ?: run {
    messager.e("Kotlin sources dir not found")
    ""
  }

/**
 * Trying to hack our way through to get some test utilities to be
 * visible from our android-module tests.
 */
@VisibleForTesting
val List<BasicAnnotationProcessor>
  .asProcessorList: List<Processor> get() = this
