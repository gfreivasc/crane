package com.gabrielfv.basicsample.collapsibleflow

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollapsibleResult(
  val confirmedAge: Int
) : Parcelable
