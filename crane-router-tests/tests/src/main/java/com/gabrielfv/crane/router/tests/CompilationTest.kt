package com.gabrielfv.crane.router.tests

import com.gabrielfv.crane.router.kapt.JavacRouterProcessor
import com.gabrielfv.crane.router.ksp.KspRouterProcessor
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.runners.Parameterized
import java.io.File
import javax.annotation.processing.Processor

abstract class CompilationTest(
  private val provider: ProcessorProvider<Any>
) {
  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data() = listOf(
      arrayOf(
        KspProcessorProvider(listOf { KspRouterProcessor.Provider() })
      ),
      arrayOf(
        JavacProcessorProvider(listOf { JavacRouterProcessor() })
      )
    )
  }

  protected fun compile(folder: File, vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    val processors = provider.provide()
    val kspProcessors = processors.filterIsInstance<SymbolProcessorProvider>()
    val kaptProcessors = processors.filterIsInstance<Processor>()
    return KotlinCompilation()
      .apply {
        workingDir = folder
        inheritClassPath = true
        if (kspProcessors.isNotEmpty()) {
          symbolProcessorProviders = kspProcessors
        } else if (kaptProcessors.isNotEmpty()) {
          annotationProcessors = kaptProcessors
        }
        sources = sourceFiles.asList()
        verbose = false
        kspIncremental = true
      }.compile()
  }
}

