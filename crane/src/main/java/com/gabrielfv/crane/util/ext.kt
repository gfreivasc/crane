package com.gabrielfv.crane.util

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route

internal fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) {
  val transaction = beginTransaction()
  try {
    transaction.block()
  } finally {
    transaction.commit()
  }
}

internal fun Fragment.placeKey(route: Route) {
  arguments = bundleOf(Crane.KEY_CRANE_PARAMS to route)
}
