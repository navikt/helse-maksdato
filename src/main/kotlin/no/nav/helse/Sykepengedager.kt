package no.nav.helse

import java.time.*
import java.time.temporal.ChronoUnit.*

const val maxTilgjengeligeDager = 248

fun maksdato(førsteSykepengedag: LocalDate, tidligerePerioder: List<Tidsperiode>): LocalDate {
   val dagerForbrukt = dagerForbrukt(førsteSykepengedag, tidligerePerioder)
   val dagerTilgode = if (dagerForbrukt > maxTilgjengeligeDager) 0 else maxTilgjengeligeDager - dagerForbrukt
   return nWeekdaysFrom(dagerTilgode - 1, førsteSykepengedag)
}

fun dagerForbrukt(førsteSykepengedag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   val sisteTreÅr = Tidsperiode(førsteSykepengedag.minusYears(3), førsteSykepengedag)
   val førstePeriodeMed26UkersMellomrom =
      indexFørstePeriodeMed26UkersMellomrom(førsteSykepengedag, tidligerePerioder)

   return tidligerePerioder.subList(0, førstePeriodeMed26UkersMellomrom)
      .flatMap { it.days() }
      .filterNot(::isWeekend)
      .filter{ it.isWithin(sisteTreÅr) }
      .count()
}

fun indexFørstePeriodeMed26UkersMellomrom(førsteSykepengedag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   return tidligerePerioder.withIndex()
      .filter {
         val førsteDagNestePeriode = if (it.index == 0) førsteSykepengedag else tidligerePerioder[it.index - 1].fom
         val sisteDagForrigePeriode = if (it.index == 0) tidligerePerioder[it.index].tom else tidligerePerioder[it.index].tom
         WEEKS.between(sisteDagForrigePeriode, førsteDagNestePeriode) >= 26 }
      .map { it.index }
      .firstOrNull() ?:tidligerePerioder.size
}

data class Tidsperiode(val fom: LocalDate, val tom: LocalDate) {

   init {
       if (tom.isBefore(fom)) throw IllegalArgumentException("tom cannot be before fom, $tom is before $fom")
   }

   fun contains(day: LocalDate): Boolean = !(day.isBefore(fom) || day.isAfter(tom))

   fun nrOfDays() = if (fom == tom ) 1 else DAYS.between(fom, tom) + 1

   fun days(): List<LocalDate> = (0 until nrOfDays()).map(fom::plusDays)

}

fun LocalDate.isWithin(period: Tidsperiode) = period.contains(this)
