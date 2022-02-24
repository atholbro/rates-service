package net.aholbrook.norm

import kotlin.reflect.KClass

sealed interface DbError

object NoResults : DbError {
    override fun toString() = "No results"
}

object TooManyResults : DbError {
    override fun toString() = "Too many results"
}

open class ColumnMappingError(
    val column: Field,
    val throwable: Throwable? = null,
) : DbError {
    override fun toString() = "column \"$column\" mapping failed ${throwable?.message}"
}

class ColumnNotFound(
    column: Field,
) : ColumnMappingError(column) {
    override fun toString() = "column \"$column\" not found"
}

class ColumnWrongType(
    column: Field,
    val expectedType: KClass<*>,
    val actualType: KClass<*>,
) : ColumnMappingError(column) {
    override fun toString() = "column \"$column\" expected $expectedType but found $actualType"
}
