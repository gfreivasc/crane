package com.gabrielfv.crane.core

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.Transition
import com.gabrielfv.crane.core.affinity.AffinityManager
import com.gabrielfv.crane.core.affinity.AffinityRoute
import com.gabrielfv.crane.util.placeKey
import com.gabrielfv.crane.util.transaction

internal interface Navigator {

  @MainThread
  fun push(
    vararg route: Route,
    transition: Transition = Transition.FRAGMENT_OPEN
  )

  @MainThread
  fun push(
    vararg route: Route,
    @AnimRes @AnimatorRes enter: Int,
    @AnimRes @AnimatorRes exit: Int = 0,
    @AnimRes @AnimatorRes popEnter: Int = 0,
    @AnimRes @AnimatorRes popExit: Int = 0
  )

  @MainThread
  fun push(
    vararg route: Route,
    @StyleRes transitionStyle: Int
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
      if (fragmentManager.fragments.isEmpty()) {
        commitPush(root)
      } else {
        stackRecord++
      }
    }

    override fun push(
      vararg route: Route,
      enter: Int,
      exit: Int,
      popEnter: Int,
      popExit: Int
    ) {
      pushCalculateTag(*route) {
        setCustomAnimations(enter, exit, popEnter, popExit)
      }
    }

    override fun push(vararg route: Route, transition: Transition) {
      pushCalculateTag(*route) {
        setTransition(transition.value)
      }
    }

    override fun push(vararg route: Route, transitionStyle: Int) {
      pushCalculateTag(*route) {
        setTransitionStyle(transitionStyle)
      }
    }

    private fun pushCalculateTag(
      vararg routes: Route,
      transitionSetup: FragmentTransaction.() -> Unit
    ) {
      val taggedRoutes = routes.map { route ->
        when (route) {
          is AffinityRoute -> route.tag
          else -> null
        } to route
      }
      commitPush(taggedRoutes, transitionSetup)
    }

    private fun commitPush(route: Route) {
      commitPush(listOf(null to route)) { }
    }

    private fun commitPush(
      taggedRoutes: List<Pair<String?, Route>>,
      transitionSetup: FragmentTransaction.() -> Unit
    ) {
      val taggedFragments = taggedRoutes.map { (tag, route) ->
        tag to fetchFragment(route)
          .apply { placeKey(route) }
      }
      fragmentManager.transaction {
        transitionSetup()
        taggedFragments.forEach { (tag, fragment) ->
          affinityManager.push(tag)
          replace(containerId, fragment)
          if (stackRecord > 0) addToBackStack(tag)
        }
        stackRecord++
      }
    }

    override fun pop(): Boolean {
      if (stackRecord <= 1) return false
      affinityManager.popRegular()
      fragmentManager.popBackStack()
      stackRecord--
      return true
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
