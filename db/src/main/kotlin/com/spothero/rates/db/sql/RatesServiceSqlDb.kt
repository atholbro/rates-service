package com.spothero.rates.db.sql

import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.repositories.base._public.RatesRepository
import com.spothero.rates.db.repositories.sql._public.RatesSqlRepository
import net.aholbrook.norm.jasync.postgres.JasyncPostgresConnectionPool
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.SqlDatabaseConfig

private object DefaultPool {
    var config: SqlDatabaseConfig = SqlDatabaseConfig.fromEnvironment()

    val connection by lazy {
        JasyncPostgresConnectionPool(config)
    }
}

class RatesServiceSqlDb(
    private val connection: Connection<*, *> = DefaultPool.connection,
) : RatesServiceDb {
    override val rates: RatesRepository by lazy { RatesSqlRepository(connection) }

    override suspend fun <T> inTransaction(block: suspend RatesServiceDb.() -> T): T {
        return connection.inTransaction {
            block(RatesServiceSqlDb(it))
        }
    }
}
