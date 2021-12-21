package com.gabrielfv.samples.complete.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
import com.gabrielfv.crane.core.Crane
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    RootModule::class,
    FragmentsModule::class
  ]
)
interface RootComponent {
  fun crane(): Crane
  fun fragmentFactory(): FragmentFactory

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance applicationContext: Context): RootComponent
  }
}
