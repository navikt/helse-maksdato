package no.nav.helse

import java.time.*

data class Grunnlagsdata(val førsteFraværsdag: LocalDate,
                         val førsteSykepengedag: LocalDate,
                         val personensAlder: Int,
                         val yrkesstatus: Yrkesstatus,
                         val tidligerePerioder: List<Tidsperiode>)
