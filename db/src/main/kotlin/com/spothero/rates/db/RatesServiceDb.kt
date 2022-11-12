package com.spothero.rates.db

import com.spothero.rates.db.repositories.base.public.RatesRepository

interface RatesServiceDb {
    val rates: RatesRepository

    suspend fun <T> inTransaction(block: suspend RatesServiceDb.() -> T): T
}
