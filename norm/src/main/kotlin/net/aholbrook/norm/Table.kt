package net.aholbrook.norm

import com.github.michaelbull.result.Result
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.util.prependIfNotBlank

data class Table<Entity>(
    val schema: String? = null,
    val name: String,
    val entityEncoder: (entity: Entity) -> List<Any?>,
    val entityDecoder: (row: RowData, prefix: String) -> Result<Entity, DbError>,
) {
    private lateinit var _fields: List<Field.Named>

    val fields: Map<String, Field.Named> by lazy {
        _fields.associateBy { it.field }
    }

    val primaryKeyFields: Map<String, Field.Named> by lazy {
        fields.filter { it.value.primaryKey }
    }

    val fqn: String = name.prependIfNotBlank(schema, ".")

    class Builder<Entity> {
        var schema: String? = null
        lateinit var name: String
        lateinit var entityEncoder: (entity: Entity) -> List<Any?>
        lateinit var entityDecoder: (row: RowData, prefix: String) -> Result<Entity, DbError>
        val fields = mutableListOf<Field.Named>()

        inline fun <reified ValueType> field(name: String, primaryKey: Boolean = false) {
            fields.add(Field.Named(name, primaryKey = primaryKey))
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
