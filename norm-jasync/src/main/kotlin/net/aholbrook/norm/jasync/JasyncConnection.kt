package net.aholbrook.norm.jasync

import com.github.jasync.sql.db.SuspendingConnection
import net.aholbrook.norm.jasync.result.JasyncQueryResult
import net.aholbrook.norm.jasync.result.JasyncResultSet
import net.aholbrook.norm.jasync.result.JasyncRowData
import net.aholbrook.norm.sql.Connection

class JasyncConnection(
    private val connection: SuspendingConnection,
    override val isInTransaction: Boolean = false,
) : Connection<JasyncRowData, JasyncResultSet> {
    override suspend fun query(sql: String, values: List<Any?>, release: Boolean) =
        JasyncQueryResult(connection.sendPreparedStatement(sql, values, release))

    override suspend fun update(sql: String, values: List<Any?>, release: Boolean) =
        JasyncQueryResult(connection.sendPreparedStatement(sql, values, release)).rowsAffected

    override suspend fun <T> inTransaction(block: suspend (Connection<JasyncRowData, JasyncResultSet>) -> T): T =
        if (!isInTransaction) {
            block(this)
        } else {
            connection.inTransaction { block(JasyncConnection(it, true)) }
        }
}
