package net.aholbrook.norm.jdbi.result

import net.aholbrook.norm.sql.result.RowData
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

fun ResultSet?.toRowData(): JdbiRowData {
    val data = mutableListOf<Any?>()
    val columnMapping = mutableMapOf<String, Int>()

    for (index in 1..(this?.metaData?.columnCount ?: 0)) {
        data.add(this?.getObject(index))

        this?.metaData?.getColumnLabel(index)?.let {
            columnMapping[it] = data.size - 1
        }
    }

    return JdbiRowData(
        row = this?.row ?: 0,
        data = data,
        columnMapping = columnMapping,
    )
}

class JdbiRowData internal constructor(
    override val row: Int,
    private val data: List<Any?>,
    private val columnMapping: Map<String, Int>,
) : RowData {
    override val columns: Int get() = columnMapping.keys.size
    val columnNames: Set<String> by lazy { columnMapping.keys }

    override fun get(index: Int): Any? = data[index]
    override fun get(column: String): Any? = columnMapping[column]?.let { data[it] }

    override fun getDate(column: Int): LocalDateTime? =
        getAs<Date>(column)?.toInstant()?.let {
            LocalDateTime.ofInstant(it, ZoneId.systemDefault())
        }

    override fun getDate(column: String): LocalDateTime? =
        getAs<Date>(column)?.toInstant()?.let {
            LocalDateTime.ofInstant(it, ZoneId.systemDefault())
        }
}
