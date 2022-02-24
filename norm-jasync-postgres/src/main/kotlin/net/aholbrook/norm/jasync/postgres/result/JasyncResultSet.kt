package net.aholbrook.norm.jasync.postgres.result

import net.aholbrook.norm.sql.result.ResultSet

class JasyncResultSet(
    private val resultSet: com.github.jasync.sql.db.ResultSet,
) : ResultSet<JasyncRowData> {
    override fun columnNames() = resultSet.columnNames()

    override val size = resultSet.size

    override fun contains(element: JasyncRowData) =
        resultSet.contains(element.rowData)

    override fun containsAll(elements: Collection<JasyncRowData>) =
        resultSet.containsAll(elements.map { it.rowData })

    override fun get(index: Int): JasyncRowData = JasyncRowData(resultSet[index])

    override fun indexOf(element: JasyncRowData) = resultSet.indexOf(element.rowData)

    override fun isEmpty() = resultSet.isEmpty()

    override fun iterator(): Iterator<JasyncRowData> =
        resultSet.iterator().asSequence().map { JasyncRowData(it) }.iterator()

    override fun lastIndexOf(element: JasyncRowData) = resultSet.lastIndexOf(element.rowData)

    override fun listIterator() =
        resultSet.listIterator().asSequence().map { JasyncRowData(it) }.toList().listIterator()

    override fun listIterator(index: Int) =
        resultSet.listIterator(index).asSequence().map { JasyncRowData(it) }.toList().listIterator()

    override fun subList(fromIndex: Int, toIndex: Int) =
        resultSet.subList(fromIndex, toIndex).map { JasyncRowData(it) }
}
