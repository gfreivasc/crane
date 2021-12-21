package com.gabrielfv.samples.complete.notes

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class DateParser @Inject constructor() {

  private enum class Formats(val pattern: String) {
    SAME_DAY("h:mma"),
    SAME_MONTH("EEE hha"),
    SAME_YEAR("EEE MMM d"),
    DEFAULT("MMM yyyy")
  }

  fun parse(offsetDateTime: OffsetDateTime): String {
    val local = offsetDateTime.toLocalDateTime()
    val format = getFormat(local)
    return DateTimeFormatter.ofPattern(format.pattern, Locale.getDefault())
      .format(local)
  }

  private fun getFormat(local: LocalDateTime): Formats {
    val now = LocalDateTime.now()

    return if (local.year != now.year) {
      Formats.DEFAULT
    } else if (local.month != now.month) {
      Formats.SAME_YEAR
    } else if (local.dayOfMonth != now.dayOfMonth) {
      Formats.SAME_MONTH
    } else {
      Formats.SAME_DAY
    }
  }
}
