package com.gabrielfv.samples.complete.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeNote(
  val id: Long,
  val title: String,
  val preview: String,
  val lastModified: String
) : Parcelable

@Parcelize
data class HomeState(
  val loading: Boolean,
  val notes: List<HomeNote>
) : Parcelable
