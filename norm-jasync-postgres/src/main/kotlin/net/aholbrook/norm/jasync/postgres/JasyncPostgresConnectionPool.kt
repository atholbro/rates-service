package net.aholbrook.norm.jasync.postgres

import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import net.aholbrook.norm.jasync.JasyncConnection
import net.aholbrook.norm.jasync.result.JasyncQueryResult
import net.aholbrook.norm.jasync.result.JasyncResultSet
import net.aholbrook.norm.jasync.result.JasyncRowData
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.SqlDatabaseConfig
import net.aholbrook.norm.sql.result.QueryResult

typealias JasyncConnectionPool<T> = com.github.jasync.sql.db.pool.ConnectionPool<T>

class JasyncPostgresConnectionPool(
    val config: SqlDatabaseConfig
) : Connection<JasyncRowData, JasyncResultSet> {
    private val pool: JasyncConnectionPool<PostgreSQLConnection> by lazy {
        with(config) {
            com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder.createConnectionPool(
                "jdbc:postgresql://$host:$port/$database?user=$username&password=$password",
            )
        }
    }

    override suspend fun query(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): QueryResult<JasyncRowData, JasyncResultSet> =
        JasyncQueryResult(pool.asSuspending.sendPreparedStatement(sql, values, release))

    override suspend fun update(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): Long =
        JasyncQueryResult(pool.asSuspending.sendPreparedStatement(sql, values, release)).rowsAffected

    override suspend fun <T> inTransaction(
        block: suspend (Connection<JasyncRowData, JasyncResultSet>) -> T,
    ): T =
        pool.asSuspending.inTransaction { block(JasyncConnection(it)) }
}
