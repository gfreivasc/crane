package com.gabrielfv.crane.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class RoutedBy(val value: KClass<*>)
