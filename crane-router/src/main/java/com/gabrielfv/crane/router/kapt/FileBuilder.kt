package com.gabrielfv.crane.router.kapt

import java.io.File
import java.io.OutputStream
import java.nio.file.Files

internal object FileBuilder {

  fun forClass(srcDir: String, pkg: String, className: String): OutputStream {
    val path = "$srcDir/${pkg.replace('.', '/')}"
    val dir = File(path).apply { mkdir() }
    val target =  with (dir.toPath()) {
      Files.createDirectories(this)
      resolve("$className.kt")
    }
    return Files.newOutputStream(target)
  }
}
