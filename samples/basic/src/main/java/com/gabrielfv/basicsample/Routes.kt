package com.gabrielfv.basicsample

import com.gabrielfv.crane.core.Route
import kotlinx.parcelize.Parcelize

object Routes {
  @Parcelize
  object First : Route(FirstFragment::class)

  @Parcelize
  data class Second(val count: Int) : Route(SecondFragment::class)
}
