package com.gabrielfv.crane.router

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
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

fun Messager.d(message: String, element: Element? = null) {
  if (element != null) {
    printMessage(Diagnostic.Kind.MANDATORY_WARNING, message, element)
  } else {
    printMessage(Diagnostic.Kind.MANDATORY_WARNING, message)
  }
}

val ProcessingEnvironment.kaptGeneratedSourcesDir: String get() =
  options[KAPT_KOTLIN_GENERATED] ?: run {
    messager.e("Kotlin sources dir not found")
    ""
  }
