package net.aholbrook.norm.jasync.postgres.result

import net.aholbrook.norm.sql.result.RowData

class JasyncRowData(internal val rowData: com.github.jasync.sql.db.RowData) : RowData {
    override fun get(index: Int) = rowData[index]
    override fun get(column: String) = rowData[column]
    override fun rowNumber() = rowData.rowNumber()
    override fun getInt(column: Int) = rowData.getInt(column)
    override fun getInt(column: String) = rowData.getInt(column)
    override fun getLong(column: Int) = rowData.getLong(column)
    override fun getLong(column: String) = rowData.getLong(column)
    override fun getBoolean(column: Int) = rowData.getBoolean(column)
    override fun getBoolean(column: String) = rowData.getBoolean(column)
    override fun getByte(column: String) = rowData.getByte(column)
    override fun getByte(column: Int) = rowData.getByte(column)
    override fun getDate(column: Int) = rowData.getDate(column)
    override fun getDate(column: String) = rowData.getDate(column)
    override fun getFloat(column: Int) = rowData.getFloat(column)
    override fun getFloat(column: String) = rowData.getFloat(column)
    override fun getDouble(column: Int) = rowData.getDouble(column)
    override fun getDouble(column: String) = rowData.getDouble(column)
    override fun getString(column: Int) = rowData.getString(column)
    override fun getString(column: String) = rowData.getString(column)
    override fun <T> getAs(column: Int) = rowData.getAs<T>(column)
    override fun <T> getAs(column: String) = rowData.getAs<T>(column)
    override val size = rowData.size
    override fun contains(element: Any?) = rowData.contains(element)
    override fun containsAll(elements: Collection<Any?>) = rowData.containsAll(elements)
    override fun indexOf(element: Any?) = rowData.indexOf(element)
    override fun isEmpty() = rowData.isEmpty()
    override fun iterator() = rowData.iterator()
    override fun lastIndexOf(element: Any?) = rowData.lastIndexOf(element)
    override fun listIterator() = rowData.listIterator()
    override fun listIterator(index: Int) = rowData.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = rowData.subList(fromIndex, toIndex)
}
