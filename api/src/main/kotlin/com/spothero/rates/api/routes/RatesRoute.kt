package com.spothero.rates.api.routes

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.runCatching
import com.spothero.rates.api.model.ApiError
import com.spothero.rates.api.model.ApiRates
import com.spothero.rates.api.model.fromService
import com.spothero.rates.api.model.toService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import com.spothero.rates.api.RatesApplication.Companion.instance as app

fun Application.ratesModule() {
    suspend fun PipelineContext<Unit, ApplicationCall>.getAll() {
        // Get all rates from core service:
        app.ratesService.getAll()
            // Convert to API format:
            .map { ApiRates.fromService(it) }
            // Handle success/errors, sending HTTP response:
            .mapBoth(
                success = {
                    call.respond(it)
                },

                failure = {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ApiError(it.toString()),
                    )
                }
            )
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.replace() {
        // Get rates from HTTP body:
        runCatching { call.receive<ApiRates>() }
            // Convert API rate format to service format:
            .map { it.toService() }
            // Replace all rates with the new list of rates:
            .andThen { app.ratesService.replaceAll(it) }
            // Convert service rate format back into API rate format:
            .map { ApiRates.fromService(it) }
            // Handle success/errors, sending HTTP response:
            .mapBoth(
                success = {
                    call.respond(it)
                },

                failure = {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ApiError(it.toString()),
                    )
                }
            )
    }

    routing {
        get("/rates") { getAll() }
        put("/rates") { replace() }
    }
}
