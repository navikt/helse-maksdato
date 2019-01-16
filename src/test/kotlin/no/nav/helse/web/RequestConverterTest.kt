package no.nav.helse.web

import no.nav.helse.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import org.json.*
import java.time.*

object RequestConverterTest: Spek({

   val jsonWithNoPeriods = """
       {
         "førsteFraværsdag": "2019-01-15",
         "førsteSykepengedag": "2019-01-20",
         "tidligerePerioder": []
       }
   """.trimIndent()

   val jsonWithPeriods = """
       {
         "førsteFraværsdag": "2019-01-15",
         "førsteSykepengedag": "2019-01-20",
         "tidligerePerioder": [
            {"fom": "2019-01-20", "tom": "2019-01-21"},
            {"fom": "2019-01-05", "tom": "2019-01-07"}
          ]
       }
   """.trimIndent()

   val invalidJson = """{"bogus": 2}"""

   describe("convert from json") {

      given("an org.json object with no previous periods") {
         it("should create a MaksdatoRequest with the no periods") {
            val expected = MaksdatoRequest(
               LocalDate.of(2019, 1, 15),
               LocalDate.of(2019, 1, 20),
               emptyList()
            )
            val actual = JSONObject(jsonWithNoPeriods).toMaksdatoRequest() as Success
            expected `should equal` actual.request
         }
      }

      given("an org.json object with previous periods") {
         it("should create a MaksdatoRequest with periods") {
            val expected = MaksdatoRequest(
               LocalDate.of(2019, 1, 15),
               LocalDate.of(2019, 1, 20),
               listOf(
                  Tidsperiode(LocalDate.of(2019, 1, 20), LocalDate.of(2019, 1, 21)),
                  Tidsperiode(LocalDate.of(2019, 1, 5), LocalDate.of(2019, 1, 7))
               )
            )
            val actual = JSONObject(jsonWithPeriods).toMaksdatoRequest() as Success
            expected `should equal` actual.request
         }
      }

      given("invalid json") {
         it("returns Failure") {
            val convertResult = JSONObject(invalidJson).toMaksdatoRequest()
            convertResult `should be instance of` Failure::class
         }
      }

   }

})
