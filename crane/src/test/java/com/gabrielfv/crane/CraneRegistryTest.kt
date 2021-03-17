package com.gabrielfv.crane

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.core.CraneRegistry
import com.gabrielfv.crane.core.RouteMap
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CraneRegistryTest {
  private val routeMap: RouteMap = mapOf(
    A::class to AFragment::class
  )
  private val fragmentTransaction: FragmentTransaction = mockk(relaxed = true)
  private val fragManager: FragmentManager = mockk {
    every { beginTransaction() } returns fragmentTransaction
    every { backStackEntryCount } returns 0
    every { fragmentFactory } returns mockk(relaxed = true)
  }
  private val activity: FragmentActivity = mockk {
    every { supportFragmentManager } returns fragManager
  }
  private val containerViewId = 0

  @Test
  fun onRegistry_init() {
    val crane = CraneRegistry.Default()
    val savedState: Bundle = mockk(relaxed = true)
    every { fragManager.fragmentFactory } returns mockk {
      every {
        instantiate(any(), eq("com.gabrielfv.crane.AFragment"))
      } returns AFragment()
    }

    crane.init(activity, containerViewId) {
      map(routeMap)
      root(A(1))
      savedState(savedState)
    }

    verify {
      fragmentTransaction.replace(
        eq(containerViewId),
        withArg { fragment ->
          assertThat(fragment).isInstanceOf(AFragment::class.java)
        }
      )
    }
  }

  @Test
  fun onGetInstance_returns() {
    val crane = CraneRegistry.Default()
    val savedState: Bundle = mockk(relaxed = true)
    val expected = crane.init(activity, containerViewId) {
      map(routeMap)
      root(A(1))
      savedState(savedState)
    }

    val result = crane.getInstance()

    assertThat(result).isEqualTo(expected)
  }

  @Test
  fun failsOnGetInstance_withoutInit() {
    val crane = CraneRegistry.Default()

    val result = try {
      crane.getInstance()
    } catch (ex: Throwable) {
      ex
    }

    assertThat(result).isInstanceOf(IllegalStateException::class.java)
    assertThat((result as Throwable).message)
      .contains("Instance of Crane not available, make sure it has been initialized.")
  }
}
