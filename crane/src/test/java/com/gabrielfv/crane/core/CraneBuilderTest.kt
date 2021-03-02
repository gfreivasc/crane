package com.gabrielfv.crane.core

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.A
import com.gabrielfv.crane.AFragment
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.result.ResultRegistry
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
  fun onBuild_withoutCreate() {
    val subject = Crane.Builder()

    val result = try {
      subject.map(routeMap)
        .root(A(1))
        .build()
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
    val message = result.let { it as IllegalArgumentException }.message
    assertThat(message).isEqualTo(
      "Cannot build crane. Make sure to call <create()> method on builder."
    )
  }

  @Test
  fun onBuild_withoutRouteMap() {
    val subject = Crane.Builder()

    val result = try {
      subject.create(activity, containerViewId)
        .root(A(1))
        .build()
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
    val message = result.let { it as IllegalArgumentException }.message
    assertThat(message).isEqualTo(
      "Cannot initialize crane without a route map. " +
        "Define one with the <route()> builder method."
    )
  }

  @Test
  fun onBuild_withoutRoot() {
    val subject = Crane.Builder()

    val result = try {
      subject.create(activity, containerViewId)
        .map(routeMap)
        .build()
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
    val message = result.let { it as IllegalArgumentException }.message
    assertThat(message).isEqualTo(
      "Cannot start Crane without a root. " +
        "Define one through the <root()> builder method."
    )
  }

  @Test
  fun onBuild_withSavedState() {
    val savedState: Bundle = mockk(relaxed = true)
    val affinityManager: AffinityManager = mockk(relaxed = true)
    val resultRegistry: ResultRegistry = mockk(relaxed = true)
    val subject = Crane.Builder(affinityManager, resultRegistry)
    every { fragManager.backStackEntryCount } returns 0
    every { fragManager.fragmentFactory } returns mockk(relaxed = true)

    subject.create(activity, containerViewId)
      .map(routeMap)
      .root(A(1))
      .savedState(savedState)
      .build()

    verify {
      affinityManager.restore(eq(savedState))
      resultRegistry.restore(eq(savedState))
    }
  }
}
