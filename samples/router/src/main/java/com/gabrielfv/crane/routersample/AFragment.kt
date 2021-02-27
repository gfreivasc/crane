package com.gabrielfv.crane.routersample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.router.RoutedBy
import com.gabrielfv.crane.routersample.databinding.AFragmentBinding
import kotlinx.parcelize.Parcelize

@Parcelize
class ARoute : Route

@RoutedBy(ARoute::class)
class AFragment : Fragment(R.layout.a_fragment) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val binding = AFragmentBinding.bind(view)
    binding.textView.setOnClickListener {
      NavReg.crane.push(BRoute())
    }
  }
}
