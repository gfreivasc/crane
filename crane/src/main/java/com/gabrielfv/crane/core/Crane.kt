package com.gabrielfv.crane.core

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.gabrielfv.crane.FragmentAnimation
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.affinity.AffinityRoute
import com.gabrielfv.crane.core.result.ResultRegistry
import com.gabrielfv.crane.util.placeKey
import com.gabrielfv.crane.util.setCustomAnimations
import com.gabrielfv.crane.util.transaction
import kotlin.reflect.KClass

typealias RouteMap = Map<KClass<out Route>, KClass<out Fragment>>

class Crane internal constructor(
  private val routeMap: RouteMap,
  private val rootActivity: FragmentActivity,
  @IdRes private val containerViewId: Int,
  private val affinityManager: AffinityManager,
  private val resultRegistry: ResultRegistry
) {
  private val fragmentManager = rootActivity.supportFragmentManager
  private var stackRecord: Int = fragmentManager.backStackEntryCount
  private val saveables = setOf(affinityManager, resultRegistry)

  internal fun setRoot(route: Route) {
    affinityManager.push(ROOT_AFFINITY_TAG)
    if (stackRecord > 0) return

    val fragment = fetchFragment(route)
    fragment.placeKey(route)
    fragmentManager.transaction {
      addToBackStack(ROOT_AFFINITY_TAG)
      replace(containerViewId, fragment)
      stackRecord++
    }
  }

  fun push(route: Route, customAnimations: FragmentAnimation = FragmentAnimation()) {
    if (stackRecord == 0) {
      setRoot(route)
      return
    }
    val fragment = fetchFragment(route)
    fragment.placeKey(route)
    fragmentManager.transaction {
      setCustomAnimations(customAnimations)
      val tag = if (route is AffinityRoute) {
        route.tag
      } else {
        null
      }
      affinityManager.push(tag)
      addToBackStack(tag)
      replace(containerViewId, fragment)
      stackRecord++
    }
  }

  fun pop(): Boolean {
    if (stackRecord == 0) return false
    affinityManager.popRegular()
    fragmentManager.popBackStack()
    return stackRecord-- > 1
  }

  fun popAffinity() {
    val affinity = affinityManager.popAffinity()
    if (affinity != null) {
      val (tag, offset) = affinity
      if (tag == ROOT_AFFINITY_TAG) rootActivity.finish()
      else {
        stackRecord -= offset
        fragmentManager.popBackStack(
          tag,
          FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
      }
    } else {
      throw IllegalStateException(
        "Crane was not supposed to run without any affinity."
      )
    }
  }

  private fun fetchFragment(route: Route): Fragment {
    val targetName = routeMap[route::class]?.java?.name
      ?: throw IllegalArgumentException(
        "Cannot push unregistered key. Register it to a Fragment."
      )
    return fragmentManager
      .fragmentFactory
      .instantiate(
        javaClass.classLoader!!,
        targetName
      )
  }

  fun <T : Parcelable> pushResult(result: T) {
    resultRegistry.push(result)
  }

  inline fun <reified T : Parcelable> fetchResult(): T? = fetchResult(T::class)

  fun <T : Parcelable> fetchResult(cls: KClass<T>): T? = resultRegistry.fetch(cls)

  fun saveInstanceState(outState: Bundle) {
    saveables.forEach { it.save(outState) }
  }

  fun restoreSavedState(savedInstanceState: Bundle) {
    saveables.forEach { it.restore(savedInstanceState) }
  }

  companion object {
    internal const val ROOT_AFFINITY_TAG = "com.gabrielfv.crane.ROOT_AFFINITY_TAG"
    internal const val KEY_CRANE_PARAMS = "com.gabrielfv.crane.KEY_CRANE_PARAMS"
  }

  class Builder {
    private var partial: Crane? = null

    fun create(
      routeMap: RouteMap,
      activity: FragmentActivity,
      @IdRes containerViewId: Int
    ) = this.apply {
      val affinityManager = AffinityManager()
      val resultRegistry = ResultRegistry()
      partial = Crane(routeMap, activity, containerViewId, affinityManager, resultRegistry)
    }

    fun root(route: Route) = this.apply {
      partial?.setRoot(route) ?: throw IllegalStateException(
        "Crane root has not been set. " +
          "Make sure to call <create()> before <root()>"
      )
    }

    fun restoreSavedState(savedInstanceState: Bundle?) = this.apply {
      if (savedInstanceState != null) {
        partial?.restoreSavedState(savedInstanceState) ?: throw IllegalStateException(
          "Crane could not restore saved state. " +
            "Make sure to call <create()> before <restoreSavedState()>"
        )
      }
    }

    fun build() = partial ?: throw IllegalStateException(
      "Crane instance has not been created. " +
        "Make sure to call <create()> before <build()>"
    )
  }
}
