package net.aholbrook.norm.sql

import net.aholbrook.norm.Field
import net.aholbrook.norm.Table
import net.aholbrook.norm.util.prependIfNotBlank

fun Field.Named.fqn(): String =
    escaped().prependIfNotBlank(table?.fqn, ".")

fun Field.Named.prefixed(prefix: String? = null): String =
    escaped().prependIfNotBlank("\"$prefix\"", ".")

fun Field.Named.aliased(prefix: String, alias: String = "${prefix}_"): String =
    "${prefixed(prefix)} \"$alias$field\""

fun Field.Named.escaped(): String = "\"$field\""

// TODO add error handing for improper field list size
fun pkWhereClause(pkFields: Collection<Field.Named>): String = when (pkFields.size) {
    0 -> ""
    1 -> "${pkFields.first().escaped()} = ?"
    else -> "(${pkFields.joinToString {it.escaped() }}) = (${(1..pkFields.size).joinToString { "?" }})"
}

fun <E> sqlGetAll(table: Table<E>): String = """
    SELECT ${table.fields.values.joinToString { it.fqn() }}
    FROM ${table.fqn}
""".trimIndent()

fun <E> sqlGetByPrimaryKey(table: Table<E>): String = """
    ${sqlGetAll(table)}
    WHERE ${pkWhereClause(table.primaryKeyFields.values)}
""".trimIndent()

fun <E> sqlInsert(table: Table<E>): String {
    val fields = table.fields.values.filter { !it.primaryKey }
    return """
        INSERT INTO ${table.fqn} (${fields.joinToString { it.escaped() }})
        VALUES (${(1..fields.size).joinToString { "?" }})
        RETURNING ${table.fields.values.joinToString { it.escaped() }};
    """.trimIndent()
}

fun <E> sqlUpdate(table: Table<E>): String {
    val fields = table.fields.values.filter { !it.primaryKey }
    return """
        UPDATE ${table.fqn}
        SET ${fields.joinToString { "${it.escaped()} = ?" }}
        WHERE ${pkWhereClause(table.primaryKeyFields.values)}
        RETURNING ${table.fields.values.joinToString { it.escaped() }};
    """.trimIndent()
}

fun <E> sqlDelete(table: Table<E>): String {
    val pkFields = table.primaryKeyFields.values
    return """
        DELETE
        FROM ${table.fqn}
        WHERE ${pkWhereClause(pkFields)}
        RETURNING ${table.fields.values.joinToString { it.escaped() }};
    """.trimIndent()
}
