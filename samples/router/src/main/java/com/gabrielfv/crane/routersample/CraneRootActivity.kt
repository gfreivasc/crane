package com.gabrielfv.crane.routersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.routersample.databinding.ActivityMainBinding

@CraneRoot
class CraneRootActivity : AppCompatActivity() {
  private lateinit var crane: Crane

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    crane = Crane.create(Router.get())
    crane.init(this, android.R.id.content, ARoute())
  }

  override fun onBackPressed() {
    if (!crane.pop()) super.onBackPressed()
  }
}
