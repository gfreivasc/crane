package com.gabrielfv.crane.router.tests

import com.gabrielfv.crane.router.asProcessorList
import com.gabrielfv.crane.router.kapt.JavacRouterProcessor
import com.gabrielfv.crane.router.ksp.KspRouterProcessor
import com.google.auto.common.BasicAnnotationProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessors
import org.junit.runners.Parameterized
import java.io.File

abstract class CompilationTest(
  private val provider: ProcessorProvider<Any>
) {
  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data() = listOf(
      arrayOf(
        KspProcessorProvider(listOf { KspRouterProcessor() })
      ),
      arrayOf(
        JavacProcessorProvider(listOf { JavacRouterProcessor() })
      )
    )
  }

  protected fun compile(folder: File, vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    val processors = provider.provide()
    val kspProcessors = processors.filterIsInstance<SymbolProcessor>()
    val kaptProcessors = processors.filterIsInstance<BasicAnnotationProcessor>()
    return KotlinCompilation()
      .apply {
        workingDir = folder
        inheritClassPath = true
        if (kspProcessors.isNotEmpty()) {
          symbolProcessors = kspProcessors
        } else if (kaptProcessors.isNotEmpty()) {
          annotationProcessors = kaptProcessors.asProcessorList
        }
        sources = sourceFiles.asList()
        verbose = false
        kspIncremental = true
      }.compile()
  }
}

