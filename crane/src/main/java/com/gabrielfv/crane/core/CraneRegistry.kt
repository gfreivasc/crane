package com.gabrielfv.crane.core

import androidx.annotation.MainThread

interface CraneRegistry {

  @MainThread
  fun create(): Crane

  @MainThread
  fun getInstance(): Crane

  class Default : CraneRegistry {
    private lateinit var instance: Crane

    override fun create(): Crane {
      return Crane().also { crane ->
        instance = crane
      }
    }

    override fun getInstance(): Crane {
      check(::instance.isInitialized) {
        "Instance of Crane not available, make sure it has been initialized."
      }
      return instance
    }
  }
}
