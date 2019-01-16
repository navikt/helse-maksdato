package no.nav.helse.web

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.response.*
import io.ktor.routing.*
import io.prometheus.client.*
import io.prometheus.client.exporter.common.*
import no.nav.helse.*
import org.slf4j.event.*
import java.time.format.*

private val collectorRegistry = CollectorRegistry.defaultRegistry

private val counter = Counter.build()
   .name("maksdato_requests")
   .help("Antall kall til maksdato-beregning")
   .register()

fun Application.maksdatoCalc() {
   install(CallLogging) {
      level = Level.INFO
   }

   install(ContentNegotiation) {
      register(ContentType.Application.Json, JsonConverter())
   }

   routing {
      maksdato()
      nais()
   }
}

fun Routing.maksdato() {
   post("/") {
      counter.inc()
      try {
         when (val input = call.receiveJson().toMaksdatoRequest()) {
            is Success -> call.respond(
               maksdato(
                  input.request.førsteFraværsdag,
                  input.request.førsteFraværsdag,
                  input.request.tidligerePerioder
               ).format(DateTimeFormatter.ISO_DATE)
            )
            is Failure -> call.respond(BadRequest, input.errMsg)
         }
      } catch (ex: Exception) {
         call.respond(BadRequest, "You did not supply valid JSON")
      }

   }
}

fun Routing.nais() {
   get("/isalive") {
      call.respondText("ALIVE", ContentType.Text.Plain)
   }

   get("/isready") {
      call.respondText("READY", ContentType.Text.Plain)
   }

   get("/metrics") {
      val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: emptySet()
      call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
         TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
      }
   }
}
