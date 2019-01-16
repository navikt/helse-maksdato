package no.nav.helse.web

sealed class ConverterResult

data class Success(val request: MaksdatoRequest): ConverterResult()
data class Failure(val errMsg: String): ConverterResult()
