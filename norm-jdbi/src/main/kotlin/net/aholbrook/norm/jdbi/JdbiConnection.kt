package net.aholbrook.norm.jdbi

import kotlinx.coroutines.runBlocking
import net.aholbrook.norm.jdbi.mapping.NormRowMapper
import net.aholbrook.norm.jdbi.result.JdbiQueryResult
import net.aholbrook.norm.jdbi.result.JdbiResultSet
import net.aholbrook.norm.jdbi.result.JdbiRowData
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.result.QueryResult
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiConnection(
    private val handle: Handle,
) : Connection<JdbiRowData, JdbiResultSet> {
    override val isInTransaction: Boolean
        get() = handle.isInTransaction

    override suspend fun query(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): QueryResult<JdbiRowData, JdbiResultSet> {
        val query = handle.createQuery(sql)

        values.forEachIndexed { index, value -> query.bindByType(index, value, value?.javaClass) }

        val rows = query
            .registerRowMapper(NormRowMapper())
            .mapTo<JdbiRowData>()
            .toList()

        return JdbiQueryResult(JdbiResultSet(rows))
    }

    override suspend fun update(
        sql: String,
        values: List<Any?>,
        release: Boolean,
    ): Long {
        val update = handle.createUpdate(sql)

        values.forEachIndexed { index, value -> update.bindByType(index, value, value?.javaClass) }

        return update.execute().toLong()
    }

    override suspend fun <T> inTransaction(block: suspend (Connection<JdbiRowData, JdbiResultSet>) -> T): T {
        return if (isInTransaction) {
            block(this)
        } else {
            handle.inTransaction<T, Exception> {
                runBlocking { block(JdbiConnection(it)) }
            }
        }
    }
}
