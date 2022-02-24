package com.spothero.rates.db.repositories.mock

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import com.spothero.rates.db.models.Rate
import com.spothero.rates.db.repositories.base._public.RateKey
import com.spothero.rates.db.repositories.base._public.RatesRepository
import io.kotest.inspectors.forAll
import net.aholbrook.norm.DbError
import net.aholbrook.norm.NoResults
import java.time.OffsetDateTime
import java.util.UUID

class RatesMockRepository : RatesRepository {
    val rates = mutableMapOf<UUID, Rate>()
    var findRatesError: DbError? = null
    var insertError: DbError? = null
    var insertAllError: DbError? = null
    var updateError: DbError? = null
    var deleteError: DbError? = null
    var clearError: DbError? = null
    var getError: DbError? = null
    var getAllError: DbError? = null

    fun reset() {
        rates.clear()

        findRatesError = null
        insertError = null
        insertAllError = null
        updateError = null
        deleteError = null
        clearError = null
        getError = null
        getAllError = null
    }

    private fun <T> entityOrError(entity: T, error: DbError?): Result<T, DbError> =
        if (error != null) {
            Err(error)
        } else {
            Ok(entity)
        }

    override suspend fun findRates(
        start: OffsetDateTime,
        end: OffsetDateTime,
    ): Result<List<Rate>, DbError> =
        entityOrError(rates.values.toList(), findRatesError)

    override suspend fun insert(entity: Rate): Result<Rate, DbError> =
        entityOrError(entity, insertError)
            .map { it.copy(id = UUID.randomUUID()) }
            .onSuccess { rates[it.id] = it }

    override suspend fun insertAll(entities: List<Rate>): Result<List<Rate>, DbError> =
        entityOrError(entities, insertAllError)
            .map { it.map { rate -> rate.copy(id = UUID.randomUUID()) } }
            .onSuccess { rates.forAll { rates[it.key] = it.value } }

    override suspend fun update(entity: Rate): Result<Rate, DbError> =
        entityOrError(entity, updateError).onSuccess { rates[it.id] = it }

    override suspend fun delete(pk: RateKey): Result<Rate, DbError> =
        entityOrError(pk, deleteError).andThen { key ->
            rates.remove(key.value!!)?.let {
                Ok(it)
            } ?: Err(NoResults)
        }

    override suspend fun clear(): Result<Long, DbError> =
        entityOrError(rates.size.toLong(), clearError).onSuccess { rates.clear() }

    override fun pk(entity: Rate): RateKey =
        RateKey(entity.id)

    override suspend fun get(pk: RateKey): Result<Rate, DbError> =
        entityOrError(pk, getError).andThen { key ->
            rates[key.value!!]?.let {
                Ok(it)
            } ?: Err(NoResults)
        }

    override suspend fun getAll(): Result<List<Rate>, DbError> =
        entityOrError(rates, getAllError).map {
            rates.values.toList()
        }
}
