package com.gabrielfv.crane.router.tests

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
  processors: ProcessorProvider<Any>
) : CompilationTest(processors) {
  private val backend = processors.toString()
  @field:Rule
  @JvmField
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
    // I don't know why the result returns empty for KSP
    // Watching  tschuchortdev/kotlin-compile-testing#129
    if (backend == "ksp") {
      throw AssumptionViolatedException(
        "Compiler result is not accounting for KSP source generation"
      )
    }
    assertThat(result.sourcesGeneratedByAnnotationProcessor)
      .isNotEmpty
    assertThat(result.sourcesGeneratedByAnnotationProcessor)
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
    val result = compile(
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
    val result = compile(
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
    // I don't know why the result returns empty for KSP
    // Watching  tschuchortdev/kotlin-compile-testing#129
    if (backend == "ksp") {
      throw AssumptionViolatedException(
        "Compiler result is not accounting for KSP source generation"
      )
    }
    assertThat(result.sourcesGeneratedByAnnotationProcessor)
      .isNotEmpty
    assertThat(result.sourcesGeneratedByAnnotationProcessor)
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
