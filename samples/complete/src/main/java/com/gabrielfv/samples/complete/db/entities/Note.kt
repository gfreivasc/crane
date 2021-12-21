package com.gabrielfv.samples.complete.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class Note(
  @PrimaryKey(autoGenerate = true) val uuid: Long = 0,
  val title: String,
  val content: String,
  val lastEdited: OffsetDateTime
)
