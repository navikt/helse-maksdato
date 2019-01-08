package no.nav.helse

import java.time.*
import java.time.temporal.ChronoUnit.*

const val maxTilgjengeligeDager = 248

fun maksdato(startDato: LocalDate, tidligerePerioder: List<Tidsperiode>): LocalDate {
   val dagerTilgode = dagerTilgode(startDato, tidligerePerioder)
   return nWeekdaysFrom(dagerTilgode, startDato)
}


fun merEnn26UkerSidenSistePeriode(startDato: LocalDate, tidligerePerioder: List<Tidsperiode>): Boolean {
   val sisteTidligereDatoMedSykepenger = tidligerePerioder.map { it.tom }.max()
   return sisteTidligereDatoMedSykepenger?.let {
      val minus26Weeks = startDato.minusWeeks(26)
      sisteTidligereDatoMedSykepenger < minus26Weeks
   } ?: true
}

fun forbrukteDagerSiste3År(startDato: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   val siste3år = Tidsperiode(startDato.minusYears(3), startDato)

   return tidligerePerioder
      .flatMap { it.days() }
      .filter { siste3år.contains(it) }
      .filterNot(::isWeekend)
      .count()
}

fun dagerTilgode(startDato: LocalDate, tidligerePerioder: List<Tidsperiode>): Int {
   if (merEnn26UkerSidenSistePeriode(startDato, tidligerePerioder)) return maxTilgjengeligeDager

   val forbrukteDagerSiste3År = forbrukteDagerSiste3År(startDato, tidligerePerioder)

   return if (forbrukteDagerSiste3År >= maxTilgjengeligeDager) 0 else maxTilgjengeligeDager - forbrukteDagerSiste3År
}

data class Tidsperiode(val fom: LocalDate, val tom: LocalDate) {

   init {
       if (tom.isBefore(fom)) throw IllegalArgumentException("tom cannot be before fom, $tom is before $fom")
   }

   fun contains(day: LocalDate): Boolean =
      !(day.isBefore(fom) || day.isAfter(tom))

   fun nrOfDays() = DAYS.between(fom, tom)

   fun days(): List<LocalDate> = (0..nrOfDays()).map(fom::plusDays)

}
