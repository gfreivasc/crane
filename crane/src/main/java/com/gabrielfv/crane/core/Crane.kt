package com.gabrielfv.crane.core

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.gabrielfv.crane.Transition
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.result.ResultRegistry
import kotlin.reflect.KClass

class Crane internal constructor(
  private val routeMap: RouteMap,
  private val affinityManager: AffinityManager,
  private val resultRegistry: ResultRegistry
) : Navigator {
  private var _navigator: Navigator? = null
  private val navigator: Navigator get() = requireNotNull(_navigator) {
    "Navigator has not been initialized. Make sure to call ${::init.name} before using Crane."
  }
  private val saved get() = setOf(affinityManager, resultRegistry)

  fun init(
    activity: FragmentActivity,
    @IdRes containerId: Int,
    root: Route,
    savedInstanceState: Bundle? = null
  ) {
    _navigator = StackNavigator(this, activity, containerId, routeMap, affinityManager, root)
    savedInstanceState?.let { restoreSavedState(it) }
  }

  override fun push(vararg route: Route, transition: Transition) {
    navigator.push(*route, transition = transition)

  }

  override fun push(
    vararg route: Route,
    enter: Int,
    exit: Int,
    popEnter: Int,
    popExit: Int
  ) {
    navigator.push(
      *route,
      enter = enter,
      exit = exit,
      popEnter = popEnter,
      popExit = popExit
    )
  }

  override fun push(vararg route: Route, transitionStyle: Int) {
    navigator.push(*route, transitionStyle = transitionStyle)
  }

  override fun pop(): Boolean = navigator.pop()

  override fun popAffinity() {
    navigator.popAffinity()
  }

  companion object : CraneRegistry by CraneRegistry.Default() {
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

  fun destroy() {
    _navigator = null
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
