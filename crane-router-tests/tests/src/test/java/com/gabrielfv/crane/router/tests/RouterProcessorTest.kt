package com.gabrielfv.crane.router.tests

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@OptIn(ExperimentalCompilerApi::class)
@RunWith(Parameterized::class)
class RouteRegistrarProcessorTest(
  processors: ProcessorProvider<Any>
) : CompilationTest(processors) {
  private val backend = processors.toString()
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  @Test
  fun routing() {
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
            package test

            import android.os.Parcel
            import androidx.fragment.app.Fragment
            import com.gabrielfv.crane.annotations.RoutedBy
            import com.gabrielfv.crane.core.Route

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
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
            package test

            import android.os.Parcel
            import androidx.fragment.app.Fragment
            import com.gabrielfv.crane.annotations.RoutedBy
            import com.gabrielfv.crane.core.Route

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
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
            package test

            import android.os.Parcel
            import com.gabrielfv.crane.annotations.RoutedBy
            import com.gabrielfv.crane.core.Route

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
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
            package test

            import android.os.Parcel
            import androidx.fragment.app.Fragment
            import com.gabrielfv.crane.annotations.RoutedBy
            import com.gabrielfv.crane.core.Route

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

  @Test
  fun sameModuleRouting() {
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
            package test

            import android.os.Parcel
            import androidx.fragment.app.Fragment
            import com.gabrielfv.crane.annotations.CraneRoot
            import com.gabrielfv.crane.annotations.RoutedBy
            import com.gabrielfv.crane.core.Route

            class R : Route {
                override fun describeContents(): Int = 0
                override fun writeToParcel(dest: Parcel?, flags: Int) { }
            }
            
            @RoutedBy(R::class)
            class A : Fragment()

            @CraneRoot
            class Root
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
    assertThat(generated).anyMatch { it.name.contains("RouteRegistrar") }
    val registrarName = generated.find { it.name.contains("RouteRegistrar") }!!
      .name
      .split(".")
      .first()
    assertThat(generated)
      .anyMatch { file ->
        file.name.contains("Router")
        with(file.readText()) {
          contains(registrarName)
        }
      }
  }
}
