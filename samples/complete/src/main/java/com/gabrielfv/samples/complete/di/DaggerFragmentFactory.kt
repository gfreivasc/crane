package com.gabrielfv.samples.complete.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import javax.inject.Provider

class DaggerFragmentFactory @Inject constructor(
  private val providers: @JvmSuppressWildcards Map<Class<*>, Provider<Fragment>>
) : FragmentFactory() {

  override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
    val clazz = Class.forName(className)
    return providers[clazz]?.get()
      ?: super.instantiate(classLoader, className)
  }
}
