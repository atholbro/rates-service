package net.aholbrook.norm.jdbi.result

import net.aholbrook.norm.sql.result.QueryResult

class JdbiQueryResult(
    override val rows: JdbiResultSet,
    override val rowsAffected: Long = rows.size.toLong(),
) : QueryResult<JdbiRowData, JdbiResultSet> {
    override val statusMessage: String? = null
}
