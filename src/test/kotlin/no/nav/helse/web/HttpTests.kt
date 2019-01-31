package no.nav.helse.web

import io.ktor.application.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*

object HttpTests: Spek({

   describe("tests with complete webapp") {

      given("a running app instance") {
         on("a get request to the nais alive endpoint") {
            it("responds with 200") {
               withTestApplication(Application::maksdatoCalc) {
                  with(handleRequest(Get, "/isalive")) {
                     response.status() `should equal` OK
                     response.content `should equal` "ALIVE"
                  }
               }
            }
         }

         on("a get request to the nais ready endpoint") {
            it("responds with 200") {
               withTestApplication(Application::maksdatoCalc) {
                  with(handleRequest(Get, "/isready")) {
                     response.status() `should equal` OK
                  }
               }
            }
         }

         on("a post request to the maksdato endpoint with invalid json") {
            it("responds with 400 and an explanation") {
               withTestApplication(Application::maksdatoCalc) {
                  with(handleRequest(Post, "/maksdato") {
                     setBody("""{"a": 1}""")
                     addHeader("Content-Type", "application/json")
                  }) {
                     response.status() `should equal` BadRequest
                     val responseTxt = response.content ?: ""
                     responseTxt.startsWith("You sent an invalid request, it should") `should equal` true
                  }
               }
            }
         }

         on("a post request to the maksdato endpoint with valid json") {
            val json = """
                         {
                          "førsteFraværsdag": "2019-01-01",
                          "førsteSykepengedag": "2019-01-15",
                          "personensAlder": 25,
                          "yrkesstatus": "ARBEIDSTAKER",
                          "tidligerePerioder": [
                             {"fom": "2018-08-01", "tom": "2018-08-01"},
                             {"fom": "2018-03-01", "tom": "2018-04-30"}
                           ]
                          }
                     """.trimIndent()

            it("responds with a date") {
               withTestApplication(Application::maksdatoCalc) {
                  with(handleRequest(Post, "/maksdato") {
                     setBody(json)
                     addHeader("Content-Type", "application/json")
                  }) {
                     response.status() `should equal` OK
                     response.content ?: "" `should equal` "2020-10-25"
                  }
               }
            }
         }

         on("a post request to the dagertilgode endpoint with valid json") {
            val json = """
                         {
                          "førsteFraværsdag": "2019-01-01",
                          "førsteSykepengedag": "2019-01-15",
                          "personensAlder": 25,
                          "yrkesstatus": "ARBEIDSTAKER",
                          "tidligerePerioder": [
                             {"fom": "2018-08-01", "tom": "2018-08-01"},
                             {"fom": "2018-03-01", "tom": "2018-04-30"}
                           ]
                          }
                     """.trimIndent()

            it("responds with number of days") {
               withTestApplication(Application::maksdatoCalc) {
                  with(handleRequest(Post, "/dagertilgode") {
                     setBody(json)
                     addHeader("Content-Type", "application/json")
                  }) {
                     response.status() `should equal` OK
                     response.content ?: "" `should equal` "204"
                  }
               }
            }
         }

      }

   }

})
