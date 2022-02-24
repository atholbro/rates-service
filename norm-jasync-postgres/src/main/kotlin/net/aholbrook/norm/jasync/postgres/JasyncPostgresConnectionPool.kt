package net.aholbrook.norm.jasync.postgres

import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import net.aholbrook.norm.jasync.postgres.result.JasyncQueryResult
import net.aholbrook.norm.jasync.postgres.result.JasyncResultSet
import net.aholbrook.norm.jasync.postgres.result.JasyncRowData
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

    override suspend fun sendQuery(query: String): QueryResult<JasyncRowData, JasyncResultSet> =
        JasyncQueryResult(pool.asSuspending.sendQuery(query))

    override suspend fun sendPreparedStatement(
        query: String,
        values: List<Any?>,
        release: Boolean,
    ): QueryResult<JasyncRowData, JasyncResultSet> =
        JasyncQueryResult(pool.asSuspending.sendPreparedStatement(query, values, release))

    override suspend fun <T> inTransaction(
        block: suspend (Connection<JasyncRowData, JasyncResultSet>) -> T,
    ): T =
        pool.asSuspending.inTransaction { block(JasyncConnection(it)) }
}
