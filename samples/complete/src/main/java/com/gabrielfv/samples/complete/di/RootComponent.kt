package com.gabrielfv.samples.complete.di

import androidx.fragment.app.FragmentFactory
import com.gabrielfv.crane.core.Crane
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
}
