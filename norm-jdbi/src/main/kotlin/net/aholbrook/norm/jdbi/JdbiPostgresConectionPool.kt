package net.aholbrook.norm.jdbi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.runBlocking
import net.aholbrook.norm.jdbi.arguments.TypeHintArgumentFactory
import net.aholbrook.norm.jdbi.result.JdbiResultSet
import net.aholbrook.norm.jdbi.result.JdbiRowData
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.SqlDatabaseConfig
import net.aholbrook.norm.sql.result.QueryResult
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin

class JdbiPostgresConectionPool(
    val config: SqlDatabaseConfig,
) : Connection<JdbiRowData, JdbiResultSet> {
    private val pool: Jdbi by lazy {
        val hkConfig = HikariConfig()

        hkConfig.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
        hkConfig.addDataSourceProperty("serverName", config.host)
        hkConfig.addDataSourceProperty("portNumber", config.port)
        hkConfig.addDataSourceProperty("databaseName", config.database)
        hkConfig.username = config.username
        hkConfig.password = config.password

        Jdbi.create(HikariDataSource(hkConfig))!!.also {
            it.installPlugin(KotlinPlugin())
            it.registerArgument(TypeHintArgumentFactory)
        }
    }

    override suspend fun query(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): QueryResult<JdbiRowData, JdbiResultSet> =
        pool.withHandle<QueryResult<JdbiRowData, JdbiResultSet>, Exception> {
            runBlocking { JdbiConnection(it).query(sql, values, release) }
        }

    override suspend fun update(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): Long =
        pool.withHandle<Long, Exception> { runBlocking { JdbiConnection(it).update(sql, values, release) } }

    override suspend fun <T> inTransaction(block: suspend (Connection<JdbiRowData, JdbiResultSet>) -> T): T {
        return pool.inTransaction<T, Exception> {
            runBlocking { block(JdbiConnection(it)) }
        } ?: throw Exception("")
    }
}
