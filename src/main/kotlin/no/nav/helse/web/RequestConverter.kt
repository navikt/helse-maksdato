package no.nav.helse.web

import no.nav.helse.*
import org.json.*
import org.slf4j.*
import java.time.*
import java.time.format.*

private val log = LoggerFactory.getLogger("RequestConverter")
private val errMsg = """
    You sent an invalid request, it should look like this one:
    {
      "førsteFraværsdag": "2019-01-15",
      "førsteSykepengedag": "2019-01-20",
      "tidligerePerioder": [
         {"fom": "2019-01-20", "tom": "2018-01-21"},
         {"fom": "2019-01-05", "tom": "2018-01-07"}
       ]
    }
""".trimIndent()

fun JSONObject.toMaksdatoRequest(): ConverterResult {
   log.debug(this.toString())
   return try {
      val førsteFraværsdag = getString("førsteFraværsdag")
      val førsteSykepengedag = getString("førsteSykepengedag")
      val periodeArray = getJSONArray("tidligerePerioder")

      val tidligerePerioder = (0 until periodeArray.length())
         .map { periodeArray.get(it) as JSONObject }
         .map { it.toTidsperiode() }

      Success(MaksdatoRequest(førsteFraværsdag.toDate(), førsteSykepengedag.toDate(), tidligerePerioder))
   } catch (ex: Exception) {
      log.warn("Error while deserializing request: ${ex.message ?: "unknown error"}")
      Failure(errMsg)
   }

}

fun String.toDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun JSONObject.toTidsperiode() =
   Tidsperiode(getString("fom").toDate(), getString("tom").toDate())
