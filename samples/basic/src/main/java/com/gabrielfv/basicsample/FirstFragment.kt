package com.gabrielfv.basicsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.databinding.FirstFragmentBinding
import com.gabrielfv.crane.FragmentAnimation
import com.gabrielfv.crane.core.Crane

class FirstFragment : Fragment() {
  private val crane: Crane = Crane.getInstance()
  private var binding: FirstFragmentBinding? = null
  private var count = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FirstFragmentBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    count = savedInstanceState?.getInt(KEY_COUNTER) ?: count
    binding!!.setupView()
  }

  private fun FirstFragmentBinding.setupView() {
    textView.text = getString(R.string.pulls, count)
    button.setOnClickListener {
      crane.push(Routes.Second(count), FragmentAnimation(
        enter = R.anim.slide_in_bottom,
        exit = R.anim.slide_out_top,
        popEnter = R.anim.slide_in_top,
        popExit = R.anim.slide_out_bottom
      ))
    }
  }

  override fun onResume() {
    super.onResume()
    crane.fetchResult<SecondFragment.Result>()
      ?.updatedCounter?.let { updated ->
        count = updated + 1
        binding!!.textView.text = getString(R.string.pulls, count)
      }
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(KEY_COUNTER, count)
  }

  companion object {
    private const val KEY_COUNTER = "KEY_COUNTER"
  }
}
