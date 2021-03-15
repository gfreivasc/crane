package com.gabrielfv.crane.router.tests

/**
 * Explicit reference to [com.google.auto.common.BasicAnnotationProcessor]
 * breaks compilation due to it's underlying supertype not being present
 * in this module's classpath
 */
class JavacProcessorProvider(
  private val factories: List<() -> Any>
) : ProcessorProvider<Any> {

  override fun provide(): List<Any> {
    return factories.map { it() }
  }

  override fun toString(): String = "kapt"
}
