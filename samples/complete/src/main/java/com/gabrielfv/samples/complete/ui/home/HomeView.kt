package com.gabrielfv.samples.complete.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.gabrielfv.samples.complete.databinding.HomeFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class HomeView(
  private val controller: HomeFragment,
  private val coroutineScope: CoroutineScope,
  private val binding: HomeFragmentBinding
) {

  fun start(state: StateFlow<HomeState>) = with(binding) {
    newNote.setOnClickListener {
      controller.editNote()
    }
    notes.layoutManager = LinearLayoutManager(root.context)
    coroutineScope.launch {
      state
        .filter { it.notes.isNotEmpty() }
        .collect { state ->
          notes.adapter = HomeNotesAdapter(state.notes) {
            controller.editNote(it)
          }
        }
    }
  }
}
