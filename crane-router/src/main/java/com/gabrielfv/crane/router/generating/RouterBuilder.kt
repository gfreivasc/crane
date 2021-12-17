package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.addOriginatingElement
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
import javax.lang.model.element.Element

internal class RouterBuilder {
  private val className get() = RouterEnv.ROUTER_CLASS

  companion object {
    private const val REGISTRARS_PROP = "registrars"
  }

  fun build(
    dir: File,
    registrars: Set<String>,
    pkg: String,
    originating: Element
  ) {
    val typeSpec = TypeSpec.objectBuilder(className)
      .addOriginatingElement(originating)
    buildSpec(typeSpec, pkg, registrars).writeTo(dir)
  }

  fun build(
    filer: XFiler,
    registrars: Set<String>,
    pkg: String,
    originating: XElement
  ) {
    val typeSpec = TypeSpec.objectBuilder(className)
      .addOriginatingElement(originating)
    buildSpec(typeSpec, pkg, registrars).writeTo(filer)
  }

  private fun buildSpec(typeSpec: TypeSpec.Builder, pkg: String, registrars: Set<String>): FileSpec {
    val fileBuilder = FileSpec.builder(pkg, className)
    val registrarsProp = buildRegistrarsProp(
      registrars.map { name ->
        ClassName(RouterEnv.REGISTRARS_PACKAGE, name)
      }.toSet().mapToConstructor()
    )
    val getMethod = buildGetterMethod()
    val router = typeSpec.addModifiers(KModifier.INTERNAL)
      .addProperty(registrarsProp)
      .addFunction(getMethod)
      .build()
    return fileBuilder
      .addType(router)
      .build()
  }

  private fun buildRegistrarsProp(registrars: Set<String>): PropertySpec =
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
            |  ${registrars.joinToString(",\n  ")}
            |)
           """.trimMargin())
          .build()
      )
      .build()

  private fun Set<ClassName>.mapToConstructor() = map { "$it()" }.toSet()

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
