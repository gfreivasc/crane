package com.gabrielfv.samples.complete.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrielfv.samples.complete.db.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

  @Query("SELECT * from note")
  fun getAll(): Flow<List<Note>>

  @Query("SELECT * from note WHERE uuid = :uuid")
  suspend fun getNote(uuid: Long): Note

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: Note): Long
}
