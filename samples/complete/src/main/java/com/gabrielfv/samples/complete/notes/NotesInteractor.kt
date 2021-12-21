package com.gabrielfv.samples.complete.notes

import com.gabrielfv.samples.complete.db.NotesDao
import com.gabrielfv.samples.complete.db.entities.Note
import com.gabrielfv.samples.complete.ui.home.HomeNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class NotesInteractor @Inject constructor(
  private val notesDao: NotesDao,
  private val dateParser: DateParser
) {

  fun fetchShortList(): Flow<List<HomeNote>> {
    return notesDao.getAll()
      .map(::mapToHomeNote)
  }

  suspend fun fetchNote(uuid: Long): Note = notesDao.getNote(uuid)

  suspend fun saveNote(
    title: String,
    content: String,
    uuid: Long? = null
  ): Long {
    val now = OffsetDateTime.now()
    val note = Note(
      uuid = uuid ?: 0,
      title = title,
      content = content,
      lastEdited = now
    )
    return notesDao.insertNote(note)
  }

  private fun mapToHomeNote(input: List<Note>): List<HomeNote> {
    return input.map { note ->
      HomeNote(
        id = note.uuid,
        title = note.title,
        preview = note.content.take(240),
        lastModified = dateParser.parse(note.lastEdited)
      )
    }
  }
}
