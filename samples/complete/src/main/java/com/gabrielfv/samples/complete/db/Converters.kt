package com.gabrielfv.samples.complete.db

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object Converters {

  @TypeConverter
  fun fromISOTime(value: String): OffsetDateTime {
    return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
  }

  @TypeConverter
  fun fromDateTime(dateTime: OffsetDateTime): String {
    return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
  }
}
