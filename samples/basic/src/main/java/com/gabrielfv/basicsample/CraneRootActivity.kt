package com.gabrielfv.basicsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gabrielfv.basicsample.databinding.ActivityMainBinding
import com.gabrielfv.crane.core.Crane

class CraneRootActivity : AppCompatActivity() {
  private lateinit var crane: Crane

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    crane = Crane.Builder()
      .create(routeMap, this, binding.root.id)
      .root(FirstRoute("Gabriel"))
      .restoreSavedState(savedInstanceState)
      .build()
    NavRegistry.crane = crane
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    crane.saveInstanceState(outState)
  }

  override fun onBackPressed() {
    if (!crane.pop()) super.onBackPressed()
  }
}
