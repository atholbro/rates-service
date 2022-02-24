package com.spothero.rates.api.errors

import com.spothero.rates.core.EndBeforeStart
import com.spothero.rates.core.UnavailableRate
import io.ktor.http.HttpStatusCode

typealias CoreRateServiceError = com.spothero.rates.core.RatesServiceError

private fun statusForError(error: CoreRateServiceError): HttpStatusCode = when (error) {
    is UnavailableRate -> HttpStatusCode.NotFound
    is EndBeforeStart -> HttpStatusCode.BadRequest
    else -> HttpStatusCode.InternalServerError
}

class RateServiceError(val error: CoreRateServiceError) : HttpRequestError(statusForError(error), error) {
    override fun toString() = error.toString()
}
