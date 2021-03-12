package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.gabrielfv.crane.router.RouterEnv
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.SET
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

internal class RouterBuilder {
  private val className get() = RouterEnv.ROUTER_CLASS

  companion object {
    private const val REGISTRARS_PROP = "registrars"
  }

  fun build(
    dir: File,
    registrars: Set<String>,
    pkg: String
  ) {
    buildSpec(registrars, pkg).writeTo(dir)
  }

  fun build(
    filer: XFiler,
    registrars: Set<String>,
    pkg: String
  ) {
    buildSpec(registrars, pkg).writeTo(filer)
  }

  private fun buildSpec(registrars: Set<String>, pkg: String): FileSpec {
    val fileBuilder = FileSpec.builder(pkg, className)
    val registrarsProp = buildRegistrarsProp(
      registrars.map { name ->
        ClassName(RouterEnv.REGISTRARS_PACKAGE, name)
      }.toSet()
    )
    val getMethod = buildGetterMethod()
    val router = TypeSpec.objectBuilder(className)
      .addModifiers(KModifier.INTERNAL)
      .addProperty(registrarsProp)
      .addFunction(getMethod)
      .build()
    return fileBuilder
      .addType(router)
      .build()
  }

  private fun buildRegistrarsProp(registrars: Set<ClassName>): PropertySpec =
    PropertySpec
      .builder(
        REGISTRARS_PROP,
        SET.parameterizedBy(RouterEnv.registrarInterfaceName),
        KModifier.PRIVATE
      )
      .getter(
        FunSpec.getterBuilder()
          .addModifiers(KModifier.INLINE)
          .addCode("""
            |return setOf(
            |  ${registrars.joinToString(",\n")}()
            |)
           """.trimMargin())
          .build()
      )
      .build()

  private fun buildGetterMethod(): FunSpec =
    FunSpec.builder(RouterEnv.GETTER_METHOD)
      .addModifiers(KModifier.PUBLIC)
      .addStatement("""
        |return $REGISTRARS_PROP.flatMap { registrar ->
        |  registrar.${RouterEnv.GETTER_METHOD}().toList()
        |}.toMap()
      """.trimMargin())
      .returns(RouterEnv.routeMapName)
      .build()
}
