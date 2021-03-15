package com.gabrielfv.crane.router.tests

import com.google.devtools.ksp.processing.SymbolProcessor

class KspProcessorProvider(
  private val factories: List<() -> SymbolProcessor>
) : ProcessorProvider<SymbolProcessor> {

  override fun provide(): List<SymbolProcessor> {
    return factories.map { it() }
  }

  override fun toString(): String = "ksp"
}
