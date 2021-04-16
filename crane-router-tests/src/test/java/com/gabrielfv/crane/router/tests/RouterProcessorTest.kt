package com.gabrielfv.crane.router.tests

import com.gabrielfv.crane.router.kapt.JavacRouterProcessor
import com.gabrielfv.crane.router.ksp.KspRouterProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.AssumptionViolatedException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class RouteRegistrarProcessorTest(
  processors: ProcessorProvider<*>
) {
  private val backend = processors.toString()
  private val testCompiler = TestCompiler(processors.provide())
  @field:Rule
  @JvmField
  val temporaryFolder = TemporaryFolder()

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data() = listOf(
      arrayOf(
        KspProcessorProvider(listOf { KspRouterProcessor() })
      ),
      arrayOf(
        JavacProcessorProvider(listOf { JavacRouterProcessor() })
      )
    )
  }

  @Test
  fun routing() {
    val result = testCompiler.compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
          package test

          import android.os.Parcel
          import androidx.fragment.app.Fragment
          import com.gabrielfv.crane.core.Route
          import com.gabrielfv.crane.annotations.RoutedBy

          class Dummy : Route {
              override fun describeContents(): Int = 0
              override fun writeToParcel(dest: Parcel?, flags: Int) { }
          }

          @RoutedBy(Dummy::class)
          class A : Fragment()
        """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.OK)
    val generated = if (backend == "ksp") {
      assertThat(result.kspGeneratedSources)
        .isNotEmpty
      result.kspGeneratedSources
    } else {
      assertThat(result.sourcesGeneratedByAnnotationProcessor)
        .isNotEmpty
      result.sourcesGeneratedByAnnotationProcessor
    }
    assertThat(generated)
      .anyMatch { file ->
        file.name.contains("RouteRegistrar")
        with(file.readText()) {
          contains("class RouteRegistrar") &&
            if (backend == "ksp") {
              contains("test.Dummy::class to test.A::class")
            } else {
              contains("test.Dummy.class") && contains("test.A.class")
            }
        }
      }
  }

  @Test
  fun multipleTargets() {
    val result = testCompiler.compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
          package test

          import android.os.Parcel
          import androidx.fragment.app.Fragment
          import com.gabrielfv.crane.core.Route
          import com.gabrielfv.crane.annotations.RoutedBy

          class Dummy : Route {
              override fun describeContents(): Int = 0
              override fun writeToParcel(dest: Parcel?, flags: Int) { }
          }
          
          @RoutedBy(Dummy::class)
          class A : Fragment()

          @RoutedBy(Dummy::class)
          class B : Fragment()
        """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages).contains(
      "Route test.Dummy is routing multiple different fragments."
    )
  }

  @Test
  fun routingNonFragment() {
    val result = testCompiler.compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
          package test

          import android.os.Parcel
          import com.gabrielfv.crane.core.Route
          import com.gabrielfv.crane.annotations.RoutedBy

          class R : Route {
              override fun describeContents(): Int = 0
              override fun writeToParcel(dest: Parcel?, flags: Int) { }
          }
          
          @RoutedBy(R::class)
          class T
        """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages).contains(
      "@RoutedBy should only be used against " +
        "androidx.fragment.app.Fragment instances"
    )
  }

  @Test
  fun nestedClassRoute() {
    val result = testCompiler.compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
          package test

          import android.os.Parcel
          import androidx.fragment.app.Fragment
          import com.gabrielfv.crane.core.Route
          import com.gabrielfv.crane.annotations.RoutedBy

          object Routes {
            class R : Route {
                override fun describeContents(): Int = 0
                override fun writeToParcel(dest: Parcel?, flags: Int) { }
            }
          }
          
          @RoutedBy(Routes.R::class)
          class A : Fragment()
        """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.OK)
    val generated = if (backend == "ksp") {
      assertThat(result.kspGeneratedSources)
        .isNotEmpty
      result.kspGeneratedSources
    } else {
      assertThat(result.sourcesGeneratedByAnnotationProcessor)
        .isNotEmpty
      result.sourcesGeneratedByAnnotationProcessor
    }
    assertThat(generated)
      .anyMatch { file ->
        file.name.contains("RouteRegistrar")
        with(file.readText()) {
          contains("class RouteRegistrar") &&
            if (backend == "ksp") {
              contains("test.Routes.R::class to test.A::class")
            } else {
              contains("test.Routes.R.class") && contains("test.A.class")
            }
        }
      }
  }
}
