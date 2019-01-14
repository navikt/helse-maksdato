package no.nav.helse

import java.time.*
import java.time.temporal.ChronoUnit.*

const val maxTilgjengeligeDager = 248

fun maksdato(førsteFraværsdag: LocalDate, førsteSykepengedag: LocalDate, tidligerePerioder: List<Tidsperiode>): LocalDate {
   val dagerForbrukt = dagerForbrukt(førsteFraværsdag, tidligerePerioder)
   val dagerTilgode = if (dagerForbrukt > maxTilgjengeligeDager) 0 else maxTilgjengeligeDager - dagerForbrukt
   return nWeekdaysFrom(dagerTilgode - 1, førsteSykepengedag)
}

fun dagerForbrukt(førsteFraværsdag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   val sisteTreÅr = Tidsperiode(førsteFraværsdag.minusYears(3), førsteFraværsdag)
   val første26UkersMellomrom =
      indexFørste26UkersMellomrom(førsteFraværsdag, tidligerePerioder)

   return tidligerePerioder.subList(0, første26UkersMellomrom)
      .flatMap { it.days() }
      .filterNot(::isWeekend)
      .filter{ it.isWithin(sisteTreÅr) }
      .count()
}

fun indexFørste26UkersMellomrom(førsteFravøærsdag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   return tidligerePerioder.withIndex()
      .filter {
         val førsteDagNestePeriode = if (it.index == 0) førsteFravøærsdag else tidligerePerioder[it.index - 1].fom
         val sisteDagForrigePeriode = tidligerePerioder[it.index].tom
         WEEKS.between(sisteDagForrigePeriode, førsteDagNestePeriode) >= 26 }
      .map { it.index }
      .firstOrNull() ?:tidligerePerioder.size
}

data class Tidsperiode(val fom: LocalDate, val tom: LocalDate) {

   init {
       if (tom.isBefore(fom)) throw IllegalArgumentException("tom cannot be before fom, $tom is before $fom")
   }

   fun contains(day: LocalDate): Boolean = !(day.isBefore(fom) || day.isAfter(tom))


   fun days(): List<LocalDate> = (0 until nrOfDays()).map(fom::plusDays)

   private fun nrOfDays() = if (fom == tom ) 1 else DAYS.between(fom, tom) + 1
}

fun LocalDate.isWithin(period: Tidsperiode) = period.contains(this)
