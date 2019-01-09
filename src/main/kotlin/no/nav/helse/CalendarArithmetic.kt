package no.nav.helse

import java.time.*
import java.time.DayOfWeek.*

tailrec fun nWeekdaysFrom(n: Int, from: LocalDate): LocalDate =
   if (n == 0) from else nWeekdaysFrom(n - 1, nextWeekday(from))

tailrec fun nextWeekday(after: LocalDate): LocalDate {
   val nextDay = after.plusDays(1)
   return if (isWeekend(nextDay)) nextWeekday(nextDay) else nextDay
}

fun isWeekend(date: LocalDate): Boolean =
   date.dayOfWeek == SATURDAY || date.dayOfWeek == SUNDAY
