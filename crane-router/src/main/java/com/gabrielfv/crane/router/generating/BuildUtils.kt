package com.gabrielfv.crane.router.generating

import kotlin.reflect.KClass
import com.squareup.kotlinpoet.ClassName as KtClassName
import com.squareup.javapoet.ClassName as JClassName

fun KtClassName.toJava(): JClassName =
  JClassName.get(packageName, simpleName)

val KClass<*>.jClassName: JClassName get() =
  JClassName.get(java)
