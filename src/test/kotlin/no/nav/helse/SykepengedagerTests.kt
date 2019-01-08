package no.nav.helse

import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

object SykepengedagerTests: Spek({

   describe("calculate maksdato") {

      given("a start date and a list of earlier sykepenge days") {

         on("no prior periods") {
            it("adds the max of 248 weekdays") {
               val startDato = LocalDate.of(2019, 1, 8)
               val expected = LocalDate.of(2019, 12, 20)
               maksdato(startDato, emptyList()) `should equal` expected
            }
         }

         on("more than 26 weeks since last period") {
            val earlierPeriods = listOf(
               Tidsperiode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 3, 28))
            )
            it("adds the max of 248 weekdays") {
               val startDato = LocalDate.of(2019, 1, 8)
               val expected = LocalDate.of(2019, 12, 20)
               maksdato(startDato, earlierPeriods) `should equal` expected
            }
         }

         on("prior sykepenger history totalling more than 248 days in the last three years") {
            it("adds 0 because no more days are available") {
               val startDato = LocalDate.of(2019, 1, 8)
               val expected = LocalDate.of(2019, 1, 8)
               val earlierPeriods = listOf(
                  Tidsperiode(LocalDate.of(2018, 11, 11), LocalDate.of(2018, 12, 28)),
                  Tidsperiode(LocalDate.of(2016, 10, 10), LocalDate.of(2017, 10, 20)),
                  Tidsperiode(LocalDate.of(2016, 8, 8), LocalDate.of(2017, 9, 9))
               )
               maksdato(startDato, earlierPeriods) `should equal` expected
            }
         }

      }

   }

})
