package com.gabrielfv.crane.core

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.A
import com.gabrielfv.crane.AFragment
import com.gabrielfv.crane.Affinity
import com.gabrielfv.crane.Result
import com.gabrielfv.crane.Unregistered
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.result.ResultRegistry
import com.gabrielfv.crane.testFragmentFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CraneTest {
  private val routeMap: RouteMap = mapOf(
    A::class to AFragment::class,
    Affinity::class to AFragment::class
  )
  private val fragmentTransaction: FragmentTransaction = mockk(relaxed = true)
  private val fragManager: FragmentManager = mockk(relaxed = true) {
    every { backStackEntryCount } returns 0
    every { beginTransaction() } returns fragmentTransaction
  }
  private val activity: FragmentActivity = mockk(relaxed = true) {
    every { supportFragmentManager } returns fragManager
  }
  private val containerViewId = 0
  private val affinityManager: AffinityManager = mockk(relaxed = true)
  private val resultRegistry: ResultRegistry = mockk(relaxed = true)

  @Test
  fun onPush_affinityWithRoot() {
    val fragment = AFragment()
    val route = Affinity(1)
    every { fragManager.fragmentFactory } returns testFragmentFactory(fragment)
    val subject = instantiate()
    subject.init(activity, containerViewId, A(0))

    subject.push(route)

    verify(atLeast = 1) {
      affinityManager.push(eq(route.tag))
      fragmentTransaction.addToBackStack(route.tag)
      fragmentTransaction.replace(eq(0), eq(fragment))
    }
  }

  @Test
  fun onPop_afterPushWithBackStack() {
    val fragment = AFragment()
    every { fragManager.fragmentFactory } returns testFragmentFactory(fragment)
    val subject = instantiate()
    subject.init(activity, containerViewId, A(0))
    subject.push(A(1))

    val result = subject.pop()

    verify {
      affinityManager.popRegular()
      fragManager.popBackStack()
    }
    assertThat(result).isEqualTo(true)
  }

  @Test
  fun onPopAffinity_rootAffinity() {
    val fragment = AFragment()
    every { fragManager.fragmentFactory } returns testFragmentFactory(fragment)
    every { affinityManager.popAffinity() } returns null
    val subject = instantiate()
    subject.init(activity, containerViewId, A(0))

    subject.popAffinity()

    verify {
      activity.finish()
    }
  }

  @Test
  fun onPopAffinity_multipleAffinities() {
    val fragment = AFragment()
    val affinity = Affinity(1)
    every { fragManager.fragmentFactory } returns testFragmentFactory(fragment)
    every { affinityManager.popAffinity() } returns Pair(affinity.tag, 1)
    val subject = instantiate()
    subject.init(activity, containerViewId, A(0))
    subject.push(affinity)

    subject.popAffinity()

    verify {
      fragManager.popBackStack(
        eq(affinity.tag),
        eq(FragmentManager.POP_BACK_STACK_INCLUSIVE)
      )
    }
    verify(exactly = 0) {
      activity.finish()
    }
  }

  @Test
  fun onPush_unregistered() {
    val subject = instantiate()
    subject.init(activity, containerViewId, A(0))

    val error = try {
      subject.push(Unregistered(0))
    } catch (ex: IllegalArgumentException) {
      ex
    } as IllegalArgumentException

    assertThat(error).hasMessage(
      "Cannot push unregistered route <Unregistered(i=0)>. Register it to a Fragment."
    )
  }

  @Test
  fun onPushResult() {
    val subject = instantiate()
    val res = Result(1)

    subject.pushResult(res)

    verify { resultRegistry.push(res) }
  }

  @Test
  fun onFetchResult_inlineWithResult() {
    every { resultRegistry.fetch(eq(Result::class)) } returns Result(1)
    val subject = instantiate()

    val result = subject.fetchResult<Result>()

    assertThat(result).isNotNull
    assertThat(result!!.i).isEqualTo(1)
  }

  @Test
  fun onFetchResult_withResult() {
    every { resultRegistry.fetch(eq(Result::class)) } returns Result(1)
    val subject = instantiate()

    val result = subject.fetchResult(Result::class)

    assertThat(result).isNotNull
    assertThat(result!!.i).isEqualTo(1)
  }

  @Test
  fun onFetchResult_inlineNoResult() {
    every { resultRegistry.fetch(eq(Result::class)) } returns null
    val subject = instantiate()

    val result = subject.fetchResult<Result>()

    assertThat(result).isNull()
  }

  @Test
  fun onFetchResult_noResult() {
    every { resultRegistry.fetch(eq(Result::class)) } returns null
    val subject = instantiate()

    val result = subject.fetchResult(Result::class)

    assertThat(result).isNull()
  }

  @Test
  fun onSaveInstanceState() {
    val bundle = mockk<Bundle>()
    val subject = instantiate()

    subject.saveInstanceState(bundle)

    verify {
      resultRegistry.save(eq(bundle))
      affinityManager.save(eq(bundle))
    }
  }

  @Test
  fun onRestoreSavedState() {
    val bundle = mockk<Bundle>()
    val subject = instantiate()

    subject.restoreSavedState(bundle)

    verify {
      resultRegistry.restore(eq(bundle))
      affinityManager.restore(eq(bundle))
    }
  }

  private fun instantiate() = Crane(
    routeMap,
    affinityManager,
    resultRegistry
  )
}
