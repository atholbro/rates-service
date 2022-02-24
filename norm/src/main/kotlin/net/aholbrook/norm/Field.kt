package net.aholbrook.norm

import net.aholbrook.norm.sql.result.RowData

sealed interface Field {
    data class Indexed(val index: Int) : Field

    data class Named<ValueType>(
        val field: String,
        val table: Table<*>? = null,
        val primaryKey: Boolean = false,
        val value: ValueType? = null,
    ) : Field {
        fun withValue(value: ValueType): Named<ValueType> = this.copy(value = value)

        override fun toString(): String = field

        companion object
    }
}

fun field(index: Int) = Field.Indexed(index)

fun namedField(name: String) =
    Field.Named<Unit>(name)

fun Field.get(rowData: RowData): Any? = when (this) {
    is Field.Indexed -> rowData[this.index]
    is Field.Named<*> -> rowData[this.toString()]
}
