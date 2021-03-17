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
  private val saved get() = setOf(affinityManager, resultRegistry)

  companion object : CraneRegistry by CraneRegistry.Default() {
    internal const val ROOT_AFFINITY_TAG = "com.gabrielfv.crane.ROOT_AFFINITY_TAG"
    internal const val KEY_CRANE_PARAMS = "com.gabrielfv.crane.KEY_CRANE_PARAMS"
  }

  internal fun setRoot(route: Route) {
    val tag = ROOT_AFFINITY_TAG
    if (stackRecord > 0) {
      affinityManager.push(tag)
    } else {
      push(route, tag = tag)
    }
  }

  @MainThread
  fun push(route: Route, customAnimations: FragmentAnimation = FragmentAnimation()) {
    if (stackRecord == 0) {
      setRoot(route)
      return
    }
    val tag = if (route is AffinityRoute) {
      route.tag
    } else {
      null
    }
    push(route, customAnimations, tag)
  }

  private fun push(
    route: Route,
    customAnimations: FragmentAnimation? = null,
    tag: String? = null
  ) {
    val fragment = fetchFragment(route)
    fragment.placeKey(route)
    fragmentManager.transaction {
      if (customAnimations != null)  {
        setCustomAnimations(customAnimations)
      }
      affinityManager.push(tag)
      addToBackStack(tag)
      replace(containerViewId, fragment)
      stackRecord++
    }
  }

  @MainThread
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
    saved.forEach { it.save(outState) }
  }

  fun restoreSavedState(savedInstanceState: Bundle) {
    saved.forEach { it.restore(savedInstanceState) }
  }

  class Builder internal constructor(
    private val affinityManager: AffinityManager,
    private val resultRegistry: ResultRegistry
  ) {
    private lateinit var routeMap: RouteMap
    private lateinit var holder: CraneHolder
    private lateinit var navRoot: Route
    private var savedInstanceState: Bundle? = null

    constructor() : this(AffinityManager(), ResultRegistry())

    fun create(
      activity: FragmentActivity,
      @IdRes containerViewId: Int
    ) = this.apply {
      holder = CraneHolder(activity, containerViewId)
    }

    fun map(routeMap: RouteMap) = this.apply {
      this.routeMap = routeMap
    }

    fun root(route: Route) = this.apply {
      navRoot = route
    }

    fun savedState(savedInstanceState: Bundle?) = this.apply {
      this.savedInstanceState = savedInstanceState
    }

    @MainThread
    fun build(): Crane {
      if (!::holder.isInitialized) {
        throw IllegalArgumentException(
          "Cannot build crane. Make sure to call <create()> method on builder."
        )
      }
      if (!::routeMap.isInitialized) {
        throw IllegalArgumentException(
          "Cannot initialize crane without a route map. " +
            "Define one with the <route()> builder method."
        )
      }
      if (!::navRoot.isInitialized) {
        throw IllegalArgumentException(
          "Cannot start Crane without a root. " +
            "Define one through the <root()> builder method."
        )
      }
      return Crane(
        routeMap,
        holder.activity,
        holder.containerViewId,
        affinityManager,
        resultRegistry
      ).apply(::initialize)
    }

    private fun initialize(crane: Crane) {
      with (crane) {
        setRoot(navRoot)
        savedInstanceState?.let { savedState ->
          restoreSavedState(savedState)
        }
      }
    }

    private data class CraneHolder(
      val activity: FragmentActivity,
      @IdRes val containerViewId: Int
    )
  }
}
