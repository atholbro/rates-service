package com.spothero.rates.api

import com.spothero.rates.core.RatesService
import com.spothero.rates.db.mock.RatesServiceMockDb
import com.spothero.rates.db.repositories.mock.RatesMockRepository

// Created via Java Service Loader
// see: kotlin/com/spothero/rates/api/Application.kt
// see: resources/META-INF/services/com.spothero.rates.api.RatesApplication
class TestApplication : RatesApplication {
    private val db = RatesServiceMockDb()
    val mockRatesRepository get() = (db.rates as RatesMockRepository)

    override val ratesService by lazy {
        RatesService(db)
    }

    companion object {
        val instance = (RatesApplication.instance as TestApplication)

        fun reset() {
            (RatesApplication.instance as TestApplication).mockRatesRepository.reset()
        }
    }
}
