package com.gabrielfv.samples.complete.di

import androidx.fragment.app.FragmentFactory
import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.core.Crane
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@CraneRoot
interface RootModule {

  @Binds
  fun bindsFragmentFactory(daggerFragmentFactory: DaggerFragmentFactory): FragmentFactory

  companion object {

    @Singleton
    @Provides
    fun provideCrane(): Crane = Crane.create(Router.get())
  }
}
