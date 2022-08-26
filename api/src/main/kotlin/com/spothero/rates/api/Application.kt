package com.spothero.rates.api

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.spothero.rates.api.model.ApiRates
import com.spothero.rates.api.model.toService
import com.spothero.rates.api.routes.priceModule
import com.spothero.rates.api.routes.ratesModule
import com.spothero.rates.core.RatesService
import com.spothero.rates.core.RatesServiceError
import com.spothero.rates.core.StartupService
import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.sql.RatesServiceSqlDb
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import java.util.ServiceLoader

interface RatesApplication {
    val ratesService: RatesService
    val startupService: StartupService

    suspend fun setInitialRates(rates: ApiRates): Result<Int, RatesServiceError> =
        // Convert to service format:
        Ok(rates.toService())
            // Replace rates using service (writes to DB):
            .andThen { ratesService.replaceAll(it) }
            // Convert results to a count of inserted rates:
            .map { it.size }

    companion object {
        val instance by lazy { load() }

        private fun load(): RatesApplication =
            ServiceLoader.load(RatesApplication::class.java)
                .findFirst()
                .orElseGet { DefaultRatesApplication() }
    }
}

class DefaultRatesApplication : RatesApplication {
    private val db: RatesServiceDb = RatesServiceSqlDb()

    override val ratesService by lazy { RatesService(db) }
    override val startupService by lazy { StartupService(db) }
}

fun Application.module() {
    // Install JSON
    install(ContentNegotiation) {
        json()
    }

    priceModule()
    ratesModule()
}
