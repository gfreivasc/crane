package com.gabrielfv.samples.complete.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gabrielfv.crane.FragmentAnimation
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.samples.complete.R
import com.gabrielfv.samples.complete.databinding.HomeFragmentBinding
import com.gabrielfv.samples.complete.ui.compose.Compose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
object Home : Route

@RoutedBy(Home::class)
class HomeFragment @Inject constructor(
  private val crane: Crane
) : Fragment() {
  private var _view: HomeView? = null
  private val state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState(false, emptyList()))

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = HomeFragmentBinding.inflate(inflater, container, false)
    _view = HomeView(this, lifecycleScope, binding)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    _view!!.start(state)
    lifecycleScope.launch {
      delay(1_000)
      state.emit(state.value.copy(notes = listOf(
        HomeNote(
          id = 14L,
          title = "How to use crane",
          preview = "Crane is an engine that wires navigation based on `Route` instances to the underlying native android navigation sctucture. Currently supporting `Fragment` navigation, it uses `FragmentActivity`'s `supportFragmentManager`",
          lastModified = "Mon Dec 20"
        )
      )))
    }
  }

  fun editNote(item: HomeNote? = null) {
    crane.push(Compose(item?.id), FragmentAnimation(
      enter = R.anim.slide_in_bottom,
      popExit = R.anim.slide_out_bottom
    ))
  }

  override fun onDestroy() {
    super.onDestroy()
    _view = null
  }
}
