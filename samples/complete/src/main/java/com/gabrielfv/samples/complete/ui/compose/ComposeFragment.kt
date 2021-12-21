package com.gabrielfv.samples.complete.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import com.gabrielfv.samples.complete.R
import com.gabrielfv.samples.complete.databinding.ComposeFragmentBinding
import com.gabrielfv.samples.complete.notes.NotesInteractor
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class Compose(val noteId: Long?) : Route

@RoutedBy(Compose::class)
class ComposeFragment @Inject constructor(
  private val crane: Crane,
  private val notesInteractor: NotesInteractor
) : Fragment() {
  private var _view: ComposeView? = null
  private var noteId: Long? = null
  private val params: Compose by params()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = ComposeFragmentBinding.inflate(inflater, container, false)
    _view = ComposeView(this, binding)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    noteId = params.noteId
    _view!!.start()
    noteId?.let { id ->
      lifecycleScope.launch {
        _view!!.setNoteContents(notesInteractor.fetchNote(id))
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _view = null
  }

  fun saveNote(title: String, content: String) {
    lifecycleScope.launch {
      notesInteractor.saveNote(title, content, noteId)
    }
    crane.pop()
  }
}
