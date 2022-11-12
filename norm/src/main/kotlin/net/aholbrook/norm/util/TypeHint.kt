package net.aholbrook.norm.util

import java.sql.Types

data class TypeHint(
    val value: Any,
    val type: Int,
) {
    override fun toString() = value.toString()
}

fun Enum<*>.asEnum() = TypeHint(this, Types.OTHER)
