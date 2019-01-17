package no.nav.helse.web

import no.nav.helse.*

sealed class ConverterResult

data class Success(val grunnlag: Grunnlagsdata): ConverterResult()
data class Failure(val errMsg: String): ConverterResult()
