package com.gabrielfv.crane.router.tests

import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.router.kapt.JavacRouterWiringProcessor
import com.gabrielfv.crane.router.ksp.KspRouterWiringProcessor
import com.google.devtools.ksp.KspExperimental
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class RouterWiringProcessorTest(
  processors: ProcessorProvider<*>
) {
  private val backend: String = processors.toString()
  private val testCompiler = TestCompiler(processors.provide())
  @field:Rule
  @JvmField
  val temporaryFolder = TemporaryFolder()

  companion object {
    @KspExperimental
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data() = listOf(
      arrayOf(
        KspProcessorProvider(listOf { KspRouterWiringProcessor() })
      ),
      arrayOf(
        JavacProcessorProvider(listOf { JavacRouterWiringProcessor() })
      )
    )
  }

  @Test
  fun routesGenerated() {
    val registrar = SourceFile.kotlin(
      "registrar.kt",
      """
        package com.gabrielfv.crane.routes.registrar

        import androidx.fragment.app.Fragment
        import com.gabrielfv.crane.core.Route
        import com.gabrielfv.crane.router.RouteRegistrar
        import kotlin.reflect.KClass

        @com.gabrielfv.crane.annotations.internal.RouteRegistrar
        class RouteRegistrar_1 : RouteRegistrar {
          override fun get(): Map<KClass<out Route>, KClass<out Fragment>> = mapOf()
        }
      """
    )
    val root = SourceFile.kotlin(
      "source.kt",
      """
        package test

        import com.gabrielfv.crane.annotations.CraneRoot

        @CraneRoot
        class A
      """
    )
    val result = testCompiler.compile(
      temporaryFolder.root,
      registrar,
      root
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.OK)
    val generated = if (backend == "ksp") {
      result.kspGeneratedSources
    } else {
      result.sourcesGeneratedByAnnotationProcessor
    }
    assertThat(generated.size).isEqualTo(1)
    assertThat(generated.first())
      .matches { file ->
        with(file.readText()) {
          contains("internal object Router")
          contains("com.gabrielfv.crane.routes.registrar.RouteRegistrar_1()")
        }
      }
  }

  @Test
  fun multipleRoots() {
    val result = testCompiler.compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
          package test

          import com.gabrielfv.crane.annotations.CraneRoot

          @CraneRoot
          class A

          @CraneRoot
          class B
        """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages)
      .contains("Multiple @${CraneRoot::class.simpleName} instances found")
  }
}
