package com.gabrielfv.samples.complete

import android.app.Activity
import android.app.Application
import com.gabrielfv.samples.complete.di.DaggerRootComponent
import com.gabrielfv.samples.complete.di.RootComponent

class SampleApp : Application() {
  private lateinit var _component: RootComponent
  val component: RootComponent get() = _component

  override fun onCreate() {
    super.onCreate()
    _component = DaggerRootComponent.create()
  }
}

val Activity.daggerComponent: RootComponent get() = (application as SampleApp).component
