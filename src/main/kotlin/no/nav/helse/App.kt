package no.nav.helse

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.helse.web.*

fun main() {
   embeddedServer(Netty, 8080) {
      maksdatoCalc()
   }.start(wait = false)
}

