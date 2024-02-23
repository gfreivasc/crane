package com.gabrielfv.basicsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gabrielfv.basicsample.databinding.ActivityMainBinding
import com.gabrielfv.crane.core.Crane

class CraneRootActivity : AppCompatActivity() {
  private val crane: Crane = Crane.create(routeMap)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    crane.init(this, binding.root.id, Routes.First)
    savedInstanceState?.let { crane.restoreSavedState(it) }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    crane.saveInstanceState(outState)
  }
}
