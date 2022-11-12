package net.aholbrook.norm.sql

import net.aholbrook.norm.Field
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
