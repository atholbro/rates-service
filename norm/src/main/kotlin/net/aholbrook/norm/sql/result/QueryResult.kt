package net.aholbrook.norm.sql.result

interface QueryResult<_RowData : RowData, _ResultSet : ResultSet<_RowData>> {
    val rowsAffected: Long
    val statusMessage: String?
    val rows: _ResultSet
}
