package com.gabrielfv.samples.complete.ui.home

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabrielfv.samples.complete.databinding.HomeFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
      state.collect { onState(it) }
    }
  }

  private fun HomeFragmentBinding.onState(state: HomeState) {
    val hasNotes = state.notes.isNotEmpty()
    notes.isVisible = hasNotes
    emptyNotes.root.isVisible = !hasNotes && !state.loading
    if (hasNotes) {
      notes.adapter = HomeNotesAdapter(state.notes) {
        controller.editNote(it)
      }
    }
  }
}
