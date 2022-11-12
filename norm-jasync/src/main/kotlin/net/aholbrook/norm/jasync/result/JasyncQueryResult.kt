package net.aholbrook.norm.jasync.result

import net.aholbrook.norm.sql.result.QueryResult

class JasyncQueryResult(
    private val queryResult: com.github.jasync.sql.db.QueryResult,
) : QueryResult<JasyncRowData, JasyncResultSet> {
    override val rowsAffected: Long
        get() = queryResult.rowsAffected

    override val statusMessage: String?
        get() = queryResult.statusMessage

    override val rows: JasyncResultSet
        get() = JasyncResultSet(queryResult.rows)
}
