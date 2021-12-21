package com.gabrielfv.samples.complete.di

import androidx.fragment.app.Fragment
import com.gabrielfv.samples.complete.ui.home.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
interface FragmentsModule {

  @Binds
  @IntoMap
  @ClassKey(HomeFragment::class)
  fun bindsHomeFragment(fragment: HomeFragment): Fragment
}
