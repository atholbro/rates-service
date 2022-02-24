package com.spothero.rates.db.repositories.base._public

import com.github.michaelbull.result.Result
import com.spothero.rates.db.models.Rate
import net.aholbrook.norm.DbError
import net.aholbrook.norm.SingleKey
import net.aholbrook.norm.repositories.base.MutableRepository
import java.time.OffsetDateTime
import java.util.UUID

typealias RateKey = SingleKey<UUID>

interface RatesRepository : MutableRepository<Rate, RateKey> {
    suspend fun findRates(start: OffsetDateTime, end: OffsetDateTime): Result<List<Rate>, DbError>
}
