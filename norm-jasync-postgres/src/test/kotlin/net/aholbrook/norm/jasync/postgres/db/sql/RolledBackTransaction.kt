package net.aholbrook.norm.jasync.postgres.db.sql

import kotlinx.coroutines.runBlocking
import net.aholbrook.norm.TestDb

object RollbackException : Exception("rollback")

fun rolledBackTransaction(
    db: TestSqlDb = TestSqlDb(),
    block: suspend (TestDb) -> Unit,
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
