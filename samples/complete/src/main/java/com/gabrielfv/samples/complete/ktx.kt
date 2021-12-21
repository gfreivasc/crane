package com.gabrielfv.samples.complete

import kotlinx.coroutines.flow.MutableStateFlow

suspend inline fun <T : Any> MutableStateFlow<T>.update(transformation: (T) -> T) {
  emit(transformation(value))
}
