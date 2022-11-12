package com.spothero.rates.db.mock

import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.repositories.base.public.RatesRepository
import com.spothero.rates.db.repositories.mock.RatesMockRepository

class RatesServiceMockDb : RatesServiceDb {
    override val rates: RatesRepository by lazy { RatesMockRepository() }

    override suspend fun <T> inTransaction(block: suspend RatesServiceDb.() -> T): T =
        block(this)
}
