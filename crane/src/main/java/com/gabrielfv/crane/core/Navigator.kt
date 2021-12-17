package com.gabrielfv.crane.core

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.gabrielfv.crane.FragmentAnimation
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.affinity.AffinityRoute
import com.gabrielfv.crane.util.placeKey
import com.gabrielfv.crane.util.setCustomAnimations
import com.gabrielfv.crane.util.transaction

internal interface Navigator {

  @MainThread
  fun push(
    route: Route,
    fragmentAnimation: FragmentAnimation? = FragmentAnimation()
  )

  @MainThread
  fun pop(): Boolean

  @MainThread
  fun popAffinity()

  class Stack(
    private val activity: FragmentActivity,
    @IdRes private val containerId: Int,
    private val routeMap: RouteMap,
    private val affinityManager: AffinityManager,
    root: Route
  ) : Navigator {
    private val fragmentManager: FragmentManager = activity.supportFragmentManager
    private var stackRecord: Int = fragmentManager.backStackEntryCount

    init {
      push(root, null)
    }

    override fun push(route: Route, fragmentAnimation: FragmentAnimation?) {
      val tag = when (route) {
        is AffinityRoute -> route.tag
        else -> null
      }
      push(route, fragmentAnimation, tag)
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
        replace(containerId, fragment)
        if (stackRecord > 0) addToBackStack(tag)
        stackRecord++
      }
    }

    override fun pop(): Boolean {
      if (stackRecord == 0) return false
      affinityManager.popRegular()
      if (stackRecord > 1) fragmentManager.popBackStack()
      return stackRecord-- > 1
    }

    override fun popAffinity() {
      val affinity = affinityManager.popAffinity()
      if (affinity != null) {
        val (tag, offset) = affinity
        stackRecord -= offset
        fragmentManager.popBackStack(
          tag,
          FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
      } else {
        activity.finish()
      }
    }

    private fun fetchFragment(route: Route): Fragment {
      val targetName = routeMap[route::class]?.java?.name
        ?: throw IllegalArgumentException(
          "Cannot push unregistered route <$route>. Register it to a Fragment."
        )
      return fragmentManager
        .fragmentFactory
        .instantiate(
          javaClass.classLoader!!,
          targetName
        )
    }
  }
}
