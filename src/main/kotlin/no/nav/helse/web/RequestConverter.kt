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
      "personensAlder": 25,
      "yrkesstatus": "ARBEIDSTAKER" // eller SELVSTENDIG_NÆRINGSDRIVNDE, FRILANSER, IKKE_I_ARBEID
      "tidligerePerioder": [
         {"fom": "2019-01-20", "tom": "2018-01-21"},
         {"fom": "2019-01-05", "tom": "2018-01-07"}
       ]
    }
""".trimIndent()

fun JSONObject.toGrunnlag(): ConverterResult {
   log.debug(this.toString())
   return try {
      val førsteFraværsdag = getString("førsteFraværsdag").toDate()
      val førsteSykepengedag = getString("førsteSykepengedag").toDate()
      val personensAlder = getInt("personensAlder")
      val yrkesstatus = Yrkesstatus.valueOf(getString("yrkesstatus"))
      val periodeArray = getJSONArray("tidligerePerioder")

      val tidligerePerioder = (0 until periodeArray.length())
         .map { periodeArray.get(it) as JSONObject }
         .map { it.toTidsperiode() }

      Success(
         Grunnlagsdata(
            førsteFraværsdag,
            førsteSykepengedag,
            personensAlder,
            yrkesstatus,
            tidligerePerioder
         )
      )
   } catch (ex: Exception) {
      log.warn("Error while deserializing request: ${ex.message ?: "unknown error"}")
      Failure(errMsg)
   }

}

fun String.toDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun JSONObject.toTidsperiode() =
   Tidsperiode(getString("fom").toDate(), getString("tom").toDate())
