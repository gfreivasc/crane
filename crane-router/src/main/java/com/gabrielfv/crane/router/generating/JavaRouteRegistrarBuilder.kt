package com.gabrielfv.crane.router.generating

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.writeTo
import com.gabrielfv.crane.annotations.internal.RouteRegistrar
import com.gabrielfv.crane.router.RouterEnv
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.WildcardTypeName
import org.jetbrains.annotations.NotNull
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

internal class JavaRouteRegistrarBuilder : RouteRegistrarBuilder {
  private val routeMap get() = ParameterizedTypeName
    .get(
      Map::class.jClassName,
      ParameterizedTypeName.get(
        KClass::class.jClassName,
        WildcardTypeName.subtypeOf(RouterEnv.routeName.toJava())
      ),
      ParameterizedTypeName.get(
        KClass::class.jClassName,
        WildcardTypeName.subtypeOf(RouterEnv.fragmentName.toJava())
      )
    )

  companion object {
    private const val KCLASS_CONVERTER = "kotlin.jvm.JvmClassMappingKt"
  }

  override fun build(
    filer: XFiler,
    className: String,
    routes: Map<String, String>
  ) {
    if (routes.isEmpty()) return
    val registrar = TypeSpec.classBuilder(className)
      .addAnnotation(RouteRegistrar::class.java)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addSuperinterface(RouterEnv.registrarInterfaceName.toJava())
      .addMethod(getterMethod(routes))
      .build()
    JavaFile.builder(RouterEnv.REGISTRARS_PACKAGE, registrar)
      .build()
      .writeTo(filer)
  }

  private fun getterMethod(routes: Map<String, String>) =
    MethodSpec.methodBuilder(RouterEnv.GETTER_METHOD)
      .addAnnotation(NotNull::class.java)
      .addAnnotation(Override::class.java)
      .addModifiers(Modifier.PUBLIC)
      .addStatement("\$N map = new \$N<>()", routeMap.toString(), HashMap::class.java.name)
      .apply {
        routes.forEach { (r, f) ->
          addStatement(
            "map.put(\$1N.getKotlinClass($r.class), \$1N.getKotlinClass($f.class))",
            KCLASS_CONVERTER
          )
        }
      }
      .addStatement("return map")
      .returns(routeMap)
      .build()
}
