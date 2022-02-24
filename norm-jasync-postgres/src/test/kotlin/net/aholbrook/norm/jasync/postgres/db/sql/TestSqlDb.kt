package net.aholbrook.norm.jasync.postgres.db.sql

import net.aholbrook.norm.TestDb
import net.aholbrook.norm.jasync.postgres.JasyncPostgresConnectionPool
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestRepository
import net.aholbrook.norm.repositories.base.test.LotRepository
import net.aholbrook.norm.repositories.sql.test.CompoundKeyTestSqlRepository
import net.aholbrook.norm.repositories.sql.test.LotSqlRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.SqlDatabaseConfig

private object DefaultPool {
    var config: SqlDatabaseConfig = SqlDatabaseConfig.fromEnvironment()

    val connection by lazy {
        JasyncPostgresConnectionPool(config)
    }
}

class TestSqlDb(
    private val connection: Connection<*, *> = DefaultPool.connection,
) : TestDb {
    override val lots: LotRepository by lazy { LotSqlRepository(connection) }
    override val compoundKeyTest: CompoundKeyTestRepository by lazy {
        CompoundKeyTestSqlRepository(connection)
    }

    override suspend fun <T> inTransaction(block: suspend TestDb.() -> T): T {
        return connection.inTransaction {
            block(TestSqlDb(it))
        }
    }
}
