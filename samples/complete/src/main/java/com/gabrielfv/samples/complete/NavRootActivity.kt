package com.gabrielfv.samples.complete

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gabrielfv.samples.complete.databinding.NavRootBinding
import com.gabrielfv.samples.complete.ui.home.Home

class NavRootActivity : AppCompatActivity() {
  private val crane by lazy { daggerComponent.crane() }

  override fun onCreate(savedInstanceState: Bundle?) {
    supportFragmentManager.fragmentFactory = daggerComponent.fragmentFactory()
    super.onCreate(savedInstanceState)
    val binding = NavRootBinding.inflate(layoutInflater)
    setContentView(binding.root)
    crane.init(
      this,
      binding.navHost.id,
      Home,
      savedInstanceState
    )
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    crane.saveInstanceState(outState)
  }

  override fun onBackPressed() {
    if (!crane.pop()) super.onBackPressed()
  }
}
