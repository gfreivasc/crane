package com.gabrielfv.crane.router.tests

import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KspProcessorProvider(
  private val factories: List<() -> SymbolProcessorProvider>
) : ProcessorProvider<SymbolProcessorProvider> {

  override fun provide(): List<SymbolProcessorProvider> {
    return factories.map { it() }
  }

  override fun toString(): String = "ksp"
}
