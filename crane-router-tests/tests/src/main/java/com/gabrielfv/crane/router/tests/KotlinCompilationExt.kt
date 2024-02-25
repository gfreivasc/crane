package com.gabrielfv.crane.router.tests

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

/*
 * These are methods created to allow us to test for
 * KSP generated sources.
 * See https://github.com/tschuchortdev/kotlin-compile-testing/issues/129
 */
internal fun File.listFilesRecursively(): List<File> {
  return listFiles()?.flatMap { file ->
    if (file.isDirectory) {
      file.listFilesRecursively()
    } else {
      listOf(file)
    }
  } ?: emptyList()
}

@OptIn(ExperimentalCompilerApi::class)
internal val JvmCompilationResult.workingDir: File get() =
  outputDirectory.parentFile!!

@OptIn(ExperimentalCompilerApi::class)
val JvmCompilationResult.kspGeneratedSources: List<File> get() {
  val kspWorkingDir = workingDir.resolve("ksp")
  val kspGeneratedDir = kspWorkingDir.resolve("sources")
  val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
  val javaGeneratedDir = kspGeneratedDir.resolve("java")
  return kotlinGeneratedDir.listFilesRecursively() +
    javaGeneratedDir.listFilesRecursively()
}
