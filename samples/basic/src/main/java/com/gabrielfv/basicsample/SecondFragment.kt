package com.gabrielfv.basicsample

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.databinding.SecondFragmentBinding
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize

class SecondFragment : Fragment() {
  private val crane: Crane = Crane.getInstance()
  private var binding: SecondFragmentBinding? = null
  private val params: Routes.Second by params()
  private var count = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SecondFragmentBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    count = params.count + 1
    crane.pushResult(Result(count))
    binding!!.setupView()
  }

  private fun SecondFragmentBinding.setupView() {
    textView.text = getString(R.string.pulls, count)
    button.setOnClickListener {
      crane.pop()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  @Parcelize
  data class Result(val updatedCounter: Int) : Parcelable
}
