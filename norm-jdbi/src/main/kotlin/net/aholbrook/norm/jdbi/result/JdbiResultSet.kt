package net.aholbrook.norm.jdbi.result

import net.aholbrook.norm.sql.result.ResultSet

class JdbiResultSet(
    private val rows: List<JdbiRowData>,
) : ResultSet<JdbiRowData>, List<JdbiRowData> by rows {
    override val columnNames: List<String> get() = rows.firstOrNull()?.columnNames?.toList() ?: listOf()
}
