package no.nav.helse.web

import no.nav.helse.*
import java.time.*

data class MaksdatoRequest(
   val førsteFraværsdag: LocalDate,
   val førsteSykepengedag: LocalDate,
   val tidligerePerioder: List<Tidsperiode>
)
