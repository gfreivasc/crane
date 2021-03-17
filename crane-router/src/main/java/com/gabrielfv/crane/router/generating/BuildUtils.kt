package com.gabrielfv.crane.router.generating

import kotlin.reflect.KClass
import com.squareup.javapoet.ClassName as JClassName
import com.squareup.kotlinpoet.ClassName as KtClassName

fun KtClassName.toJava(): JClassName =
  JClassName.get(packageName, simpleName)

val KClass<*>.jClassName: JClassName get() =
  JClassName.get(java)
