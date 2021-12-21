package com.gabrielfv.samples.complete.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.samples.complete.databinding.HomeFragmentBinding
import com.gabrielfv.samples.complete.notes.NotesInteractor
import com.gabrielfv.samples.complete.ui.compose.Compose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
object Home : Route

@RoutedBy(Home::class)
class HomeFragment @Inject constructor(
  private val crane: Crane,
  private val notesInteractor: NotesInteractor
) : Fragment() {
  private var _view: HomeView? = null
  private val state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState(true, emptyList()))

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
      notesInteractor.fetchShortList()
        .collect { notes ->
          state.update { it.copy(loading = false, notes = notes) }
        }
    }
  }

  fun editNote(item: HomeNote? = null) {
    crane.push(Compose(item?.id))
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _view = null
  }
}
