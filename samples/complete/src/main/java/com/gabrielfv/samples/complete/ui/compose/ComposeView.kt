package com.gabrielfv.samples.complete.ui.compose

import com.gabrielfv.samples.complete.databinding.ComposeFragmentBinding
import com.gabrielfv.samples.complete.db.entities.Note

class ComposeView(
  private val controller: ComposeFragment,
  private val binding: ComposeFragmentBinding
) {

  fun start() = with(binding) {
    saveNote.setOnClickListener {
      controller.saveNote(
        titleField.text?.toString().orEmpty(),
        contentField.text?.toString().orEmpty()
      )
    }
  }

  fun setNoteContents(note: Note) = with(binding) {
    titleField.setText(note.title)
    contentField.setText(note.content)
  }
}
