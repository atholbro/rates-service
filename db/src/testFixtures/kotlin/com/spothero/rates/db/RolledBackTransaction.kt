package com.spothero.rates.db

import com.spothero.rates.db.sql.RatesServiceSqlDb
import kotlinx.coroutines.runBlocking

object RollbackException : Exception("rollback")

fun rolledBackTransaction(
    db: RatesServiceDb = RatesServiceSqlDb(),
    block: suspend (RatesServiceDb) -> Unit,
) = runBlocking {
    try {
        db.inTransaction {
            block(this)
            throw RollbackException
        }
    } catch (ex: RollbackException) {
        // ignore
    }
}
