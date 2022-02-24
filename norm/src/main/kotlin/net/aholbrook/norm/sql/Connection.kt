package net.aholbrook.norm.sql

import net.aholbrook.norm.sql.result.QueryResult
import net.aholbrook.norm.sql.result.ResultSet
import net.aholbrook.norm.sql.result.RowData

interface Connection<_RowData : RowData, _ResultSet : ResultSet<_RowData>> {
    val isInTransaction: Boolean get() = false

    suspend fun sendQuery(query: String): QueryResult<_RowData, _ResultSet>

    suspend fun sendPreparedStatement(
        query: String,
        values: List<Any?> = listOf(),
        release: Boolean = true,
    ): QueryResult<_RowData, _ResultSet>

    suspend fun <T> inTransaction(block: suspend (Connection<_RowData, _ResultSet>) -> T): T
}
