package com.spothero.rates.core

import com.github.michaelbull.result.getError
import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.sql.RatesServiceSqlDb
import kotlinx.coroutines.delay
import mu.KotlinLogging
import net.aholbrook.norm.DatabaseException

private val logger = KotlinLogging.logger {}

class StartupService(
    private val db: RatesServiceDb = RatesServiceSqlDb(),
) {
    suspend fun waitForDb(timeMillis: Long = 5000) {
        // Loop until we get a working DB connection. This is to work around
        // docker startup delays (yes, they actually recommend this now).
        do {
            val error = db.rates.getAll().getError()
            val result = error != null && error is DatabaseException

            if (result) {
                logger.info {
                    "No database connection ($error), waiting ${timeMillis}ms."
                }

                delay(timeMillis)
            }
        } while (result)
    }
}
