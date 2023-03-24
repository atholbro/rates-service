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

interface ColumnError : DbError {
    val column: Field
}

class ColumnMappingError(
    override val column: Field,
    val throwable: Throwable? = null,
) : ColumnError {
    override fun toString() = "column \"$column\" mapping failed ${throwable?.message}"
}

class ColumnNotFound(
    override val column: Field,
) : ColumnError {
    override fun toString() = "column \"$column\" not found"
}

class ColumnWrongType(
    override val column: Field,
    val expectedType: KClass<*>,
    val actualType: KClass<*>,
) : ColumnError {
    override fun toString() = "column \"$column\" expected $expectedType but found $actualType"
}

suspend fun <T> mapDatabaseExceptions(block: suspend () -> T): Result<T, DbError> =
    runSuspendCatching { block() }
        .mapError { DatabaseException(it) }
