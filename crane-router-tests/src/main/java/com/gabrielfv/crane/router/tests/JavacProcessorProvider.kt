package com.gabrielfv.crane.router.tests

import javax.annotation.processing.Processor

/**
 * Explicit reference to [com.google.auto.common.BasicAnnotationProcessor]
 * breaks compilation due to it's underlying supertype not being present
 * in this module's classpath
 */
class JavacProcessorProvider(
  private val factories: List<() -> Processor>
) : ProcessorProvider<Processor> {

  override fun provide(): List<Processor> {
    return factories.map { it() }
  }

  override fun toString(): String = "kapt"
}
