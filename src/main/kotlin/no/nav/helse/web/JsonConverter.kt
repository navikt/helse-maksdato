package no.nav.helse.web

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.io.*
import kotlinx.coroutines.io.jvm.javaio.*
import org.json.*
import org.slf4j.*

class JsonConverter : ContentConverter {
   private val log = LoggerFactory.getLogger("JsonContentConverter")

   override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
      val request = context.subject
      val channel = request.value as? ByteReadChannel ?: return null

      return JSONObject(channel.toInputStream().bufferedReader().readText())
   }

   override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
      val json = when (value) {
         is List<*> -> JSONArray(value)
         else -> JSONObject(value)
      }.toString()

      log.debug("sending json, {} to {}", value, json)
      return TextContent(json, contentType.withCharset(context.call.suitableCharset()))
   }

}

suspend fun ApplicationCall.receiveJson(): JSONObject = receive()
