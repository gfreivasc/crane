package com.gabrielfv.samples.complete.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gabrielfv.samples.complete.db.entities.Note

@Database(entities = [Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class NotesDb : RoomDatabase() {
  abstract fun notesDao(): NotesDao
}
