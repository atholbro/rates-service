package net.aholbrook.norm

import com.github.michaelbull.result.Result
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.util.prependIfNotBlank

data class Table<Entity>(
    val schema: String? = null,
    val name: String,
    val entityEncoder: (Entity) -> List<Any?>,
    val entityDecoder: (RowData, String) -> Result<Entity, DbError>,
) {
    private lateinit var _fields: List<Field.Named<*>>

    val fields: Map<String, Field.Named<*>> by lazy {
        _fields.associateBy { it.field }
    }

    val primaryKeyFields: Map<String, Field.Named<*>> by lazy {
        fields.filter { it.value.primaryKey }
    }

    val fqn: String = name.prependIfNotBlank(schema, ".")

    class Builder<Entity> {
        var schema: String? = null
        lateinit var name: String
        lateinit var entityDecoder: (RowData, String) -> Result<Entity, DbError>
        lateinit var entityEncoder: (Entity) -> List<Any?>
        private val fields = mutableListOf<Field.Named<*>>()

        fun <ValueType> field(name: String, primaryKey: Boolean = false) {
            fields.add(Field.Named<ValueType>(name, primaryKey = primaryKey))
        }

        internal fun build(): Table<Entity> =
            Table(schema, name, entityEncoder, entityDecoder).also { table ->
                table._fields = fields.map { it.copy(table = table) }
            }
    }
}

fun <Entity> table(block: Table.Builder<Entity>.() -> Unit): Table<Entity> {
    val builder = Table.Builder<Entity>()
    block(builder)
    return builder.build()
}
