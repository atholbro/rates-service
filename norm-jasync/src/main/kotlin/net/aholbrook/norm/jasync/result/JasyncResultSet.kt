package net.aholbrook.norm.jasync.result

import net.aholbrook.norm.sql.result.ResultSet

class JasyncResultSet private constructor(
    override val columnNames: List<String>,
    private val rows: List<JasyncRowData>,
) : ResultSet<JasyncRowData>, List<JasyncRowData> by rows {
    constructor(resultSet: com.github.jasync.sql.db.ResultSet) : this(
        columnNames = resultSet.columnNames(),
        rows = resultSet.map { JasyncRowData(it) }
    )
}
