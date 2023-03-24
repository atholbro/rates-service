package com.spothero.rates.db.repositories.base.public

import com.github.michaelbull.result.Result
import com.spothero.rates.db.models.Rate
import net.aholbrook.norm.DbError
import net.aholbrook.norm.SingleKey
import net.aholbrook.norm.repositories.Clear
import net.aholbrook.norm.repositories.Delete
import net.aholbrook.norm.repositories.GetAll
import net.aholbrook.norm.repositories.GetByPrimaryKey
import net.aholbrook.norm.repositories.Insert
import net.aholbrook.norm.repositories.PrimaryKey
import net.aholbrook.norm.repositories.Update
import java.time.OffsetDateTime
import java.util.*

typealias RateKey = SingleKey<UUID>
object RatePrimaryKey : PrimaryKey<Rate, RateKey> {
    override fun pk(entity: Rate) = RateKey(entity.id)
}

interface RatesRepository :
    PrimaryKey<Rate, RateKey>,
    GetAll<Rate>,
    GetByPrimaryKey<Rate, RateKey>,
    Insert<Rate>,
    Update<Rate>,
    Delete<Rate, RateKey>,
    Clear {
    suspend fun findRates(start: OffsetDateTime, end: OffsetDateTime): Result<List<Rate>, DbError>
}
