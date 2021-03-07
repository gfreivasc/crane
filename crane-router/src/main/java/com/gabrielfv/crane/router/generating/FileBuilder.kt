package com.gabrielfv.crane.router.generating

import java.io.File

internal object FileBuilder {

  fun srcDir(srcDir: String): File {
    return File(srcDir).apply { mkdir() }
  }
}
