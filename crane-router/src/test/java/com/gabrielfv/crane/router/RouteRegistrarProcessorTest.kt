package com.gabrielfv.crane.router

import com.gabrielfv.crane.router.ksp.KspRouterProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import com.tschuchort.compiletesting.symbolProcessors
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RouteRegistrarProcessorTest {
  @field:Rule
  @JvmField
  val temporaryFolder = TemporaryFolder()

  @Test
  @Ignore("Annotation is not being identified in test setting")
  fun multipleTargets() {
    val result = compile(
      kotlin(
        "source.kt",
        """
                    package test
    
                    import androidx.fragment.app.Fragment
                    import com.gabrielfv.crane.core.Route
                    import com.gabrielfv.crane.router.RoutedBy
    
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
      "Route Dummy is routing multiple different fragments."
    )
  }

  @Test
  @Ignore("Annotation is not being identified in test setting")
  fun multipleRoutes() {
    val result = compile(
      kotlin(
        "source.kt",
        """
                    package test
    
                    import androidx.fragment.app.Fragment
                    import com.gabrielfv.crane.core.Route
                    import com.gabrielfv.crane.router.RoutedBy
    
                    class A : Route {
                        override fun describeContents(): Int = 0
                        override fun writeToParcel(dest: Parcel?, flags: Int) { }
                    }

                    class B : Route {
                        override fun describeContents(): Int = 0
                        override fun writeToParcel(dest: Parcel?, flags: Int) { }
                    }
                    
                    @RoutedBy(A::class)
                    @RoutedBy(B::class)
                    class Dummy : Fragment()
                """
      )
    )
    assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages).contains(
      "Fragment Dummy is annotated with @RoutedBy more than once."
    )
  }

  @Test
  @Ignore("Annotation is not being identified in test setting")
  fun routingNonFragment() {
    val result = compile(
      kotlin(
        "source.kt",
        """
                    package test

                    import com.gabrielfv.crane.core.Route
                    import com.gabrielfv.crane.router.RoutedBy
    
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

  private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    return KotlinCompilation()
      .apply {
        workingDir = temporaryFolder.root
        inheritClassPath = true
        symbolProcessors = listOf(KspRouterProcessor())
        sources = sourceFiles.asList()
        verbose = false
      }.compile()
  }
}
