package no.nav.helse

import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

object SykepengedagerTests: Spek({

   describe("calculate maksdato") {

      given("a start date and a list of earlier sykepenge days") {
         on("more than 26 weeks since last time") {
            it("calculates the max of 248 days") {
               val førsteSykepengedag = LocalDate.of(2019, 1, 15)
               val førsteFraværsdag = LocalDate.of(2019, 1, 1)
               val earlierPeriods = listOf(
                  Tidsperiode(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 2, 28))
               )
               val expected = LocalDate.of(2019, 12, 26)
               maksdato(førsteFraværsdag, førsteSykepengedag, earlierPeriods) `should equal` expected
            }
         }

         on("no prior periods") {
            it("calculates the max of 248 days") {
               val førsteSykepengedag = LocalDate.of(2019, 1, 15)
               val førsteFraværsdag = LocalDate.of(2019, 1, 1)
               val earlierPeriods = emptyList<Tidsperiode>()
               val expected = LocalDate.of(2019, 12, 26)
               maksdato(førsteFraværsdag, førsteSykepengedag, earlierPeriods) `should equal` expected
            }
         }

         on("all periods less than 26 weeks apart") {
            it("subtracts all days from previous periods") {
               val førsteSykepengedag = LocalDate.of(2019, 1, 15)
               val førsteFraværsdag = LocalDate.of(2019, 1, 1)
               val earlierPeriods = listOf(
                  Tidsperiode(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 1)),
                  Tidsperiode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 30))
               )
               val expected = LocalDate.of(2019, 10, 25)
               maksdato(førsteFraværsdag, førsteSykepengedag, earlierPeriods) `should equal` expected
            }
         }

         on("only first period has gap less than 26 weeks") {
            it("subtracts only the days from that first period") {
               val førsteSykepengedag = LocalDate.of(2019, 1, 15)
               val førsteFraværsdag = LocalDate.of(2019, 1, 1)
               val earlierPeriods = listOf(
                  Tidsperiode(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 1)),
                  Tidsperiode(LocalDate.of(2017, 10, 1), LocalDate.of(2018, 1, 30))
               )
               val expected = LocalDate.of(2019, 12, 25)
               maksdato(førsteFraværsdag, førsteSykepengedag, earlierPeriods) `should equal` expected
            }
         }

         on("several periods are less than 26 weeks apart") {
            it("subtracts all days from those periods") {
               val førsteSykepengedag = LocalDate.of(2019, 1, 15)
               val førsteFraværsdag = LocalDate.of(2019, 1, 1)
               val earlierPeriods = listOf(
                  Tidsperiode(LocalDate.of(2018, 9, 1), LocalDate.of(2018, 9, 3)),
                  Tidsperiode(LocalDate.of(2018, 1, 31), LocalDate.of(2018, 4, 30)),
                  Tidsperiode(LocalDate.of(2017, 6, 20), LocalDate.of(2017, 8, 20)),
                  Tidsperiode(LocalDate.of(2017, 1, 20), LocalDate.of(2017, 1, 25)),
                  Tidsperiode(LocalDate.of(2016, 8, 20), LocalDate.of(2016, 8, 25)),
                  Tidsperiode(LocalDate.of(2016, 4, 20), LocalDate.of(2016, 4, 30)),
                  Tidsperiode(LocalDate.of(2015, 5, 30), LocalDate.of(2016, 1, 1))
               )
               val expected = LocalDate.of(2019, 7, 3)
               maksdato(førsteFraværsdag, førsteSykepengedag, earlierPeriods) `should equal` expected
            }
         }
      }

   }

})
