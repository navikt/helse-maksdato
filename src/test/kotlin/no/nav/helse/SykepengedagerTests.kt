package no.nav.helse

import no.nav.helse.Yrkesstatus.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

object SykepengedagerTests: Spek({

   describe("calculate maksdato") {

      given("a start date and a list of earlier sykepenge days") {
         on("more than 26 weeks since last time") {
            it("calculates the max of 248 days") {
               val grunnlag = Grunnlagsdata(
                  LocalDate.of(2019, 1, 1),
                  LocalDate.of(2019, 1, 15),
                  25,
                  ARBEIDSTAKER,
                  listOf(
                     Tidsperiode(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 2, 28))
                  )
               )
               val expected = LocalDate.of(2019, 12, 26)
               maksdato(grunnlag) `should equal` expected
            }
         }

         on("no prior periods") {
            it("calculates the max of 248 days") {
               val grunnlag = Grunnlagsdata(
                  LocalDate.of(2019, 1, 1),
                  LocalDate.of(2019, 1, 15),
                  25,
                  ARBEIDSTAKER,
                  emptyList<Tidsperiode>()
               )
               val expected = LocalDate.of(2019, 12, 26)
               maksdato(grunnlag) `should equal` expected
            }
         }

         on("all periods less than 26 weeks apart") {
            it("subtracts all days from previous periods") {
               val grunnlag = Grunnlagsdata(
                  LocalDate.of(2019, 1, 1),
                  LocalDate.of(2019, 1, 15),
                  25,
                  ARBEIDSTAKER,
                  listOf(
                     Tidsperiode(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 1)),
                     Tidsperiode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 30))
                  )
               )
               val expected = LocalDate.of(2019, 10, 25)
               maksdato(grunnlag) `should equal` expected
            }
         }

         on("only first period has gap less than 26 weeks") {
            it("subtracts only the days from that first period") {
               val grunnlag = Grunnlagsdata(
                  LocalDate.of(2019, 1, 1),
                  LocalDate.of(2019, 1, 15),
                  25,
                  ARBEIDSTAKER,
                  listOf(
                     Tidsperiode(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 1)),
                     Tidsperiode(LocalDate.of(2017, 10, 1), LocalDate.of(2018, 1, 30))
                  )
               )
               val expected = LocalDate.of(2019, 12, 25)
               maksdato(grunnlag) `should equal` expected
            }
         }

         on("some periods are less than 26 weeks apart") {
            it("subtracts all days from those periods") {
               val grunnlag = Grunnlagsdata(
                  LocalDate.of(2019, 1, 1),
                  LocalDate.of(2019, 1, 15),
                  25,
                  ARBEIDSTAKER,
                  listOf(
                     Tidsperiode(LocalDate.of(2018, 9, 1), LocalDate.of(2018, 9, 3)),
                     Tidsperiode(LocalDate.of(2018, 1, 31), LocalDate.of(2018, 4, 30)),
                     Tidsperiode(LocalDate.of(2017, 6, 20), LocalDate.of(2017, 8, 20)),
                     Tidsperiode(LocalDate.of(2017, 1, 20), LocalDate.of(2017, 1, 25)),
                     Tidsperiode(LocalDate.of(2016, 8, 20), LocalDate.of(2016, 8, 25)),
                     Tidsperiode(LocalDate.of(2016, 4, 20), LocalDate.of(2016, 4, 30)),
                     Tidsperiode(LocalDate.of(2015, 5, 30), LocalDate.of(2016, 1, 1))
                  )
               )
               val expected = LocalDate.of(2019, 7, 3)
               maksdato(grunnlag) `should equal` expected
            }
         }
      }

   }

   describe("determine max nr of available days") {

      given("a person's age and occupasional status") {
         on("person between 67 and 70 years old") {
            it("is 60 days regardless of yrkesstatus") {
               (67..70).forEach { age ->
                  maxTilgjengeligeDager(age, ARBEIDSTAKER) `should equal` 60
               }
            }
         }

         on("working person less than 67 years old") {
            it("is 250 days") {
               maxTilgjengeligeDager(25, IKKE_I_ARBEID) `should equal` 250
            }
         }

         on("non-working person less than 67 years old") {
            it("is 248 days") {
               listOf(ARBEIDSTAKER, SELVSTENDIG_NÆRINGSDRIVENDE, FRILANSER).forEach { yrkesstatus ->
                  maxTilgjengeligeDager(25, yrkesstatus) `should equal` 248
               }
            }
         }
      }

   }

})
