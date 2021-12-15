package com.gabrielfv.crane.core

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.annotation.MainThread
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

class Crane internal constructor(
  private val routeMap: RouteMap,
  private val affinityManager: AffinityManager,
  private val resultRegistry: ResultRegistry
) : Navigator {
  private lateinit var navigator: Navigator
  private val saved get() = setOf(affinityManager, resultRegistry)

  fun init(
    activity: FragmentActivity,
    @IdRes containerId: Int,
    root: Route
  ) {
    init(Navigator.Stack(activity, containerId, routeMap, affinityManager, root))
  }

  private fun init(navigator: Navigator) {
    this.navigator = navigator
  }

  override fun push(route: Route, fragmentAnimation: FragmentAnimation?) {
    navigator.push(route, fragmentAnimation)
  }

  override fun pop(): Boolean = navigator.pop()

  override fun popAffinity() {
    navigator.popAffinity()
  }

  companion object : CraneRegistry by CraneRegistry.Default() {
    internal const val ROOT_AFFINITY_TAG = "com.gabrielfv.crane.ROOT_AFFINITY_TAG"
    internal const val KEY_CRANE_PARAMS = "com.gabrielfv.crane.KEY_CRANE_PARAMS"
  }

  fun <T : Parcelable> pushResult(result: T) {
    resultRegistry.push(result)
  }

  inline fun <reified T : Parcelable> fetchResult(): T? = fetchResult(T::class)

  fun <T : Parcelable> fetchResult(cls: KClass<T>): T? = resultRegistry.fetch(cls)

  fun saveInstanceState(outState: Bundle) {
    saved.forEach { it.save(outState) }
  }

  fun restoreSavedState(savedInstanceState: Bundle) {
    saved.forEach { it.restore(savedInstanceState) }
  }

  class Factory internal constructor(
    private val affinityManager: AffinityManager,
    private val resultRegistry: ResultRegistry
  ) {

    constructor() : this(AffinityManager(), ResultRegistry())

    fun create(routeMap: RouteMap): Crane {
      return Crane(routeMap, affinityManager, resultRegistry)
    }
  }
}
