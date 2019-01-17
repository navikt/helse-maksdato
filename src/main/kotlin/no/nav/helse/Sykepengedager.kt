package no.nav.helse

import no.nav.helse.Yrkesstatus.*
import java.time.*
import java.time.temporal.ChronoUnit.*

fun maksdato(grunnlag: Grunnlagsdata): LocalDate {
   val maxTilgjengeligeDager = maxTilgjengeligeDager(grunnlag.personensAlder, grunnlag.yrkesstatus)
   val dagerForbrukt = dagerForbrukt(grunnlag.førsteFraværsdag, grunnlag.tidligerePerioder)
   val dagerTilgode = if (dagerForbrukt > maxTilgjengeligeDager) 0 else maxTilgjengeligeDager - dagerForbrukt
   return nWeekdaysFrom(dagerTilgode - 1, grunnlag.førsteSykepengedag)
}

fun maxTilgjengeligeDager(personensAlder: Int, yrkesstatus: Yrkesstatus) =
   when {
      personensAlder in (67..70) -> 60
      yrkesstatus == IKKE_I_ARBEID -> 250
      else -> 248
   }

fun dagerForbrukt(førsteFraværsdag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   if (tidligerePerioder.isEmpty()) return 0

   val sisteTreÅr = Tidsperiode(førsteFraværsdag.minusYears(3), førsteFraværsdag)
   val første26UkersMellomrom =
      første26UkersMellomrom(førsteFraværsdag, tidligerePerioder)

   return tidligerePerioder.subList(0, første26UkersMellomrom)
      .flatMap { it.days() }
      .filterNot(::isWeekend)
      .filter{ it.isWithin(sisteTreÅr) }
      .count()
}

fun første26UkersMellomrom(førsteFravøærsdag: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
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
