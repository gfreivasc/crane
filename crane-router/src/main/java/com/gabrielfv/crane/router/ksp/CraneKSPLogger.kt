package com.gabrielfv.crane.router.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import org.jetbrains.kotlin.analyzer.AnalysisResult
import java.util.concurrent.atomic.AtomicBoolean

class CraneKSPLogger(
  private val delegate: KSPLogger
): KSPLogger by delegate {
  private val hasError = AtomicBoolean(false)

  override fun error(message: String, symbol: KSNode?) {
    delegate.error(message, symbol)
    hasError.set(true)
  }

  fun reportErrors() {
    if (hasError.get()) {
      throw AnalysisResult.CompilationErrorException()
    }
  }
}
