package com.spothero.rates.api.routes

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapError
import com.spothero.rates.api.errors.DateTimeParseError
import com.spothero.rates.api.errors.RateServiceError
import com.spothero.rates.api.model.ApiError
import com.spothero.rates.api.model.toPrice
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import com.spothero.rates.api.RatesApplication.Companion.instance as app

private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun Application.priceModule() {
    suspend fun PipelineContext<Unit, ApplicationCall>.lookup() =
        PriceQuery.ofQueryParameters(call.request.queryParameters)
            .andThen { app.ratesService.lookup(it.start, it.end).mapError { err -> RateServiceError(err) } }
            .mapBoth(
                success = {
                    call.respond(it.toPrice())
                },

                failure = {
                    call.respond(
                        status = it.status,
                        message = ApiError(it.toString()),
                    )
                }
            )

    routing {
        get("/price") { lookup() }
    }
}

data class PriceQuery(
    val start: OffsetDateTime,
    val end: OffsetDateTime,
) {
    companion object {
        fun ofQueryParameters(query: Parameters): Result<PriceQuery, DateTimeParseError> = binding {
            Ok(
                PriceQuery(
                    start = DateTimeParseError.catch("unable to parse start date") {
                        OffsetDateTime.parse(query["start"], formatter)
                    }.bind(),

                    end = DateTimeParseError.catch("unable to parse end date") {
                        OffsetDateTime.parse(query["end"], formatter)
                    }.bind(),
                ),
            ).bind()
        }
    }
}
