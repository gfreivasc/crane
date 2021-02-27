package com.gabrielfv.crane.core

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.A
import com.gabrielfv.crane.AFragment
import com.gabrielfv.crane.testFragmentFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@Suppress("CAST_NEVER_SUCCEEDS")
class CraneBuilderTest {
  private val routeMap: RouteMap = mapOf(
    A::class to AFragment::class
  )
  private val fragmentTransaction: FragmentTransaction = mockk(relaxed = true)
  private val fragManager: FragmentManager = mockk {
    every { beginTransaction() } returns fragmentTransaction
  }
  private val activity: FragmentActivity = mockk {
    every { supportFragmentManager } returns fragManager
  }
  private val containerViewId = 0

  @Test
  fun onSetRoot_withoutCreate() {
    val subject = Crane.Builder()

    val result = try {
      subject.root(A(1))
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalStateException::class.java)
    val message = result.let { it as IllegalStateException }.message
    assertThat(message).isEqualTo(
      "Crane root has not been set. " +
        "Make sure to call <create()> before <root()>"
    )
  }

  @Test
  fun onRestoreSavedState_withStateWithoutCreate() {
    val subject = Crane.Builder()

    val result = try {
      subject.restoreSavedState(mockk(relaxed = true))
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalStateException::class.java)
    val message = result.let { it as IllegalStateException }.message
    assertThat(message).isEqualTo(
      "Crane could not restore saved state. " +
        "Make sure to call <create()> before <restoreSavedState()>"
    )
  }

  @Test
  fun onRestoreSavedState_withoutState() {
    val subject = Crane.Builder()

    val result = try {
      subject.restoreSavedState(null)
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(Crane.Builder::class.java)
  }

  @Test
  fun onBuild_withoutCreate() {
    val subject = Crane.Builder()

    val result = try {
      subject.build()
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalStateException::class.java)
    val message = result.let { it as IllegalStateException }.message
    assertThat(message).isEqualTo(
      "Crane instance has not been created. " +
        "Make sure to call <create()> before <build()>"
    )
  }

  @Test
  fun onSetRoot_emptyBackStack() {
    val fragment = AFragment()
    every { fragManager.backStackEntryCount } returns 0
    every { fragManager.fragmentFactory } returns testFragmentFactory(fragment)
    val subject = Crane.Builder()
      .create(routeMap, activity, containerViewId)

    subject.root(A(2))

    verify {
      fragmentTransaction.addToBackStack(eq(Crane.ROOT_AFFINITY_TAG))
      fragmentTransaction.replace(eq(0), eq(fragment))
    }
  }

  @Test
  fun onSetRoot_withoutBackStack() {
    every { fragManager.backStackEntryCount } returns 1
    val subject = Crane.Builder()
      .create(routeMap, activity, containerViewId)

    subject.root(A(2))

    verify(exactly = 0) {
      fragmentTransaction.addToBackStack(eq(Crane.ROOT_AFFINITY_TAG))
      fragmentTransaction.replace(eq(0), any())
    }
  }
}
