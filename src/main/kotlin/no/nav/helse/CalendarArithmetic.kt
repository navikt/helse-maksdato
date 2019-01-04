package no.nav.helse

import java.time.*
import java.time.DayOfWeek.*

fun nWeekDaysFrom(n: Int, from: LocalDate): LocalDate {
   var temp = from
   for (i in (0 until n)) {
      temp = nextWeekDay(temp)
   }
   return temp
}

fun nextWeekDay(after: LocalDate): LocalDate {
   val tomorrow = after.plusDays(1)
   return if (isWeekend(tomorrow)) nextWeekDay(tomorrow) else tomorrow
}

fun isWeekend(date: LocalDate): Boolean =
   date.dayOfWeek == SATURDAY || date.dayOfWeek == SUNDAY
