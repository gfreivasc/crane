package com.gabrielfv.crane.core

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity

interface CraneRegistry {

  @MainThread
  fun init(
    fragmentActivity: FragmentActivity,
    @IdRes contentViewId: Int,
    builder: Crane.Builder.() -> Unit
  ): Crane

  @MainThread
  fun getInstance(): Crane

  class Default : CraneRegistry {
    private lateinit var instance: Crane

    override fun init(
      fragmentActivity: FragmentActivity,
      contentViewId: Int,
      builder: Crane.Builder.() -> Unit
    ): Crane = Crane.Builder()
      .create(fragmentActivity, contentViewId)
      .apply(builder)
      .build()
      .also { instance = it }

    override fun getInstance(): Crane {
      check(::instance.isInitialized) {
        "Instance of Crane not available, make sure it has been initialized."
      }
      return instance
    }
  }
}
