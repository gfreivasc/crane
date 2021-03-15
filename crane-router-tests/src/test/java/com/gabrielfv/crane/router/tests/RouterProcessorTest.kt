package com.gabrielfv.crane.router.tests

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class RouteRegistrarProcessorTest(
  processors: ProcessorProvider<Any>
) : CompilationTest(processors) {
  @field:Rule
  @JvmField
  val temporaryFolder = TemporaryFolder()

  @Test
  fun multipleTargets() {
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
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
    Assertions.assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    Assertions.assertThat(result.messages).contains(
      "Route Dummy is routing multiple different fragments."
    )
  }

  @Test
  fun routingNonFragment() {
    val result = compile(
      temporaryFolder.root,
      SourceFile.kotlin(
        "source.kt",
        """
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
    Assertions.assertThat(result.exitCode)
      .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    Assertions.assertThat(result.messages).contains(
      "@RoutedBy should only be used against " +
        "androidx.fragment.app.Fragment instances"
    )
  }
}
