package net.aholbrook.norm.sql.result

import java.time.LocalDateTime

interface RowData : List<Any?> {
    override operator fun get(index: Int): Any?
    operator fun get(column: String): Any?

    fun rowNumber(): Int
    fun getInt(column: Int): Int?
    fun getInt(column: String): Int?
    fun getLong(column: Int): Long?
    fun getLong(column: String): Long?
    fun getBoolean(column: Int): Boolean?
    fun getBoolean(column: String): Boolean?
    fun getByte(column: String): Byte?
    fun getByte(column: Int): Byte?
    fun getDate(column: Int): LocalDateTime?
    fun getDate(column: String): LocalDateTime?
    fun getFloat(column: Int): Float?
    fun getFloat(column: String): Float?
    fun getDouble(column: Int): Double?
    fun getDouble(column: String): Double?
    fun getString(column: Int): String?
    fun getString(column: String): String?
    fun <T> getAs(column: Int): T
    fun <T> getAs(column: String): T
}
