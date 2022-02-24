package com.spothero.rates.core

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapError
import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.models.Rate
import com.spothero.rates.db.sql.RatesServiceSqlDb
import net.aholbrook.norm.DbError
import net.aholbrook.norm.NoResults
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

sealed interface RatesServiceError

class DatabaseError(val error: DbError) : RatesServiceError {
    override fun toString() = error.toString()
}

object EndBeforeStart : RatesServiceError {
    override fun toString() = "unavailable"
}

object UnavailableRate : RatesServiceError {
    override fun toString() = "unavailable"
}

private fun firstRateOrUnavailable(rates: List<Rate>): Result<Rate, UnavailableRate> =
    if (rates.size != 1) {
        Err(UnavailableRate)
    } else {
        Ok(rates[0])
    }

private fun checkStartBeforeEnd(start: OffsetDateTime, end: OffsetDateTime): Result<Unit, RatesServiceError> =
    if (end.isAfter(start)) {
        Ok(Unit)
    } else {
        Err(EndBeforeStart)
    }

private fun checkSameDay(start: OffsetDateTime, end: OffsetDateTime): Result<Unit, RatesServiceError> =
    if (end.isAfter(start) && start.truncatedTo(ChronoUnit.DAYS) == end.truncatedTo(ChronoUnit.DAYS)) {
        Ok(Unit)
    } else {
        Err(UnavailableRate)
    }

private suspend fun <T> mapDatabaseErrors(
    block: suspend () -> Result<T, DbError>,
): Result<T, DatabaseError> =
    block().mapError { DatabaseError(it) }

private fun mapLookupErrors(
    error: RatesServiceError
): RatesServiceError =
    (error as? DatabaseError)?.let {
        when (it.error) {
            is NoResults -> UnavailableRate
            else -> it
        }
    } ?: error

class RatesService(
    private val db: RatesServiceDb = RatesServiceSqlDb(),
) {
    suspend fun getAll(): Result<List<Rate>, RatesServiceError> =
        // Get all Rates from Database:
        mapDatabaseErrors { db.rates.getAll() }

    suspend fun replaceAll(rates: List<Rate>): Result<List<Rate>, RatesServiceError> = db.inTransaction {
        // Clear existing rates:
        return@inTransaction mapDatabaseErrors { this.rates.clear() }
            // Start using rates data:
            .andThen { Ok(rates) }
            // Insert all new rates:
            .andThen { mapDatabaseErrors { this.rates.insertAll(it) } }
    }

    suspend fun lookup(start: OffsetDateTime, end: OffsetDateTime) =
        // Validate start is before end:
        checkStartBeforeEnd(start, end)
            // Validate start/end are on the same date:
            .andThen { checkSameDay(start, end) }
            // If we have valid start/end times, then look up rates in the database:
            .andThen { mapDatabaseErrors { db.rates.findRates(start, end) } }
            // The API spec calls for one rate or "unavailable", which firstRateOrUnavailable() handles:
            .andThen { firstRateOrUnavailable(it) }
            // Convert DB NoResults to UnavailableRate
            .mapError(::mapLookupErrors)

    companion object {
        /**
         * Create a production RatesService using the default SQL database.
         * @return RatesService for use in production.
         */
        fun default(): RatesService = RatesService()
    }
}
