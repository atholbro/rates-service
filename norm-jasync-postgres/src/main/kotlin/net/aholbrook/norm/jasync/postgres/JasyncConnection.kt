package net.aholbrook.norm.jasync.postgres

import com.github.jasync.sql.db.SuspendingConnection
import net.aholbrook.norm.jasync.postgres.result.JasyncQueryResult
import net.aholbrook.norm.jasync.postgres.result.JasyncResultSet
import net.aholbrook.norm.jasync.postgres.result.JasyncRowData
import net.aholbrook.norm.sql.Connection

class JasyncConnection(
    private val connection: SuspendingConnection,
    override val isInTransaction: Boolean = false,
) : Connection<JasyncRowData, JasyncResultSet> {
    override suspend fun sendQuery(query: String) =
        JasyncQueryResult(connection.sendQuery(query))

    override suspend fun sendPreparedStatement(query: String, values: List<Any?>, release: Boolean) =
        JasyncQueryResult(connection.sendPreparedStatement(query, values, release))

    override suspend fun <T> inTransaction(block: suspend (Connection<JasyncRowData, JasyncResultSet>) -> T): T =
        if (!isInTransaction) {
            block(this)
        } else {
            connection.inTransaction { block(JasyncConnection(it, true)) }
        }
}
