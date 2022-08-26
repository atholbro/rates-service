package net.aholbrook.norm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlin.reflect.KClass

sealed interface DbError

class DatabaseException(val cause: Throwable) : DbError {
    override fun toString() = cause.localizedMessage
}

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

suspend fun <T> mapDatabaseExceptions(block: suspend () -> T): Result<T, DbError> =
    runSuspendCatching { block() }
        .mapError { DatabaseException(it) }
