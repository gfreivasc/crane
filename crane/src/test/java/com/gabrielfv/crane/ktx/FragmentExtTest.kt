package com.gabrielfv.crane.ktx

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.A
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FragmentExtTest {
  private val args: Bundle = mockk()
  private val thisRef: Fragment = mockk {
    every { arguments } returns args
  }
  private val thisRefNull: Fragment = mockk {
    every { arguments } returns null
  }

  @Test
  fun onFetch_argumentIsPresent() {
    val subject = thisRef.params()
    every { args.getParcelable<Route>(eq(Crane.KEY_CRANE_PARAMS)) } returns A(1)

    val result = subject.getValue<A>(thisRef, mockk())

    assertThat(result.i).isEqualTo(1)
  }

  @Test
  fun onFetch_nothingIsPresent() {
    val subject = thisRefNull.params()
    val result = try {
      subject.getValue<A>(thisRefNull, mockk())
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalStateException::class.java)
    val message = result.let { it as IllegalStateException }.message
    assertThat(message).isEqualTo(
      "Tried to access a route that did not exist. " +
        "Has the fragment been spawned through <com.gabrielfv.crane.Crane>?"
    )
  }
}
