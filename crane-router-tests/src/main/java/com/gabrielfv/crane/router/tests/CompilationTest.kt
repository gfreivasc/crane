package com.gabrielfv.crane.router.tests

import com.gabrielfv.crane.router.asProcessorList
import com.google.auto.common.BasicAnnotationProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessors
import java.io.File

class TestCompiler(
  private val processors: List<*>
) {

  fun compile(folder: File, vararg sourceFiles: SourceFile): KotlinCompilation.Result {
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

