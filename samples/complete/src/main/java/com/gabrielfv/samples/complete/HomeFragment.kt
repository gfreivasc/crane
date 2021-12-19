package com.gabrielfv.samples.complete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import com.gabrielfv.samples.complete.databinding.HomeFragmentBinding
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class Home(val random: Int) : Route

@RoutedBy(Home::class)
class HomeFragment @Inject constructor(
  private val crane: Crane
) : Fragment() {
  private var _binding: HomeFragmentBinding? = null
  private val binding: HomeFragmentBinding get() = _binding!!
  private val params: Home by params()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = HomeFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.title.text = "The number is #${params.random}"
  }
}
