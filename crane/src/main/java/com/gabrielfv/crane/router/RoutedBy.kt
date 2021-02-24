package com.gabrielfv.crane.router

import com.gabrielfv.crane.core.Route
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class RoutedBy(val value: KClass<out Route>)
