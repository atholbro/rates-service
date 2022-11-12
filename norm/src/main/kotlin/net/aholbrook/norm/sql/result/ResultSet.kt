package net.aholbrook.norm.sql.result

interface ResultSet<_RowData : RowData> : List<_RowData> {
    val columnNames: List<String>
}
