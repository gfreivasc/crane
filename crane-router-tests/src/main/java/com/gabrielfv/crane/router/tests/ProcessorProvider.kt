package com.gabrielfv.crane.router.tests

fun interface ProcessorProvider<T> {
  fun provide(): List<T>
}
