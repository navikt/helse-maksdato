package no.nav.helse

import java.time.*
import java.time.DayOfWeek.*

tailrec fun nWeekdaysFrom(n: Int, from: LocalDate): LocalDate =
   when {
      n < 0 -> nWeekdaysFrom(n + 1, previousWeekday(from))
      n == 0 -> from
      else -> nWeekdaysFrom(n - 1, nextWeekday(from))
   }


fun nextWeekday(date: LocalDate): LocalDate {
   val daysToAdd: Long = when (date.dayOfWeek) {
      FRIDAY -> 3
      SATURDAY -> 2
      else -> 1
   }
   return date.plusDays(daysToAdd)
}

fun previousWeekday(date: LocalDate): LocalDate {
   val daysToExtract: Long = when (date.dayOfWeek) {
      MONDAY -> 3
      SUNDAY -> 2
      else -> 1
   }
   return date.minusDays(daysToExtract)
}

fun isWeekend(date: LocalDate): Boolean =
   date.dayOfWeek == SATURDAY || date.dayOfWeek == SUNDAY
