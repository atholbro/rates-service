package net.aholbrook.norm.sql.result

import java.time.LocalDateTime

interface RowData {
    val row: Int
    val columns: Int

    operator fun get(index: Int): Any?
    operator fun get(column: String): Any?

    fun getInt(column: Int): Int? = getAs(column)
    fun getInt(column: String): Int? = getAs(column)
    fun getLong(column: Int): Long? = getAs(column)
    fun getLong(column: String): Long? = getAs(column)
    fun getBoolean(column: Int): Boolean? = getAs(column)
    fun getBoolean(column: String): Boolean? = getAs(column)
    fun getByte(column: String): Byte? = getAs(column)
    fun getByte(column: Int): Byte? = getAs(column)
    fun getDate(column: Int): LocalDateTime? = getAs(column)
    fun getDate(column: String): LocalDateTime? = getAs(column)
    fun getFloat(column: Int): Float? = getAs(column)
    fun getFloat(column: String): Float? = getAs(column)
    fun getDouble(column: Int): Double? = getAs(column)
    fun getDouble(column: String): Double? = getAs(column)
    fun getString(column: Int): String? = getAs(column)
    fun getString(column: String): String? = getAs(column)

    @Suppress("UNCHECKED_CAST")
    fun <T> getAs(column: Int): T? = get(column) as? T

    @Suppress("UNCHECKED_CAST")
    fun <T> getAs(column: String): T? = get(column) as? T
}
