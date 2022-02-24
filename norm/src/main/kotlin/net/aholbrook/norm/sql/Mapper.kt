package net.aholbrook.norm.sql

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.mapResult
import com.github.michaelbull.result.runCatching
import net.aholbrook.norm.ColumnMappingError
import net.aholbrook.norm.ColumnNotFound
import net.aholbrook.norm.ColumnWrongType
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Field
import net.aholbrook.norm.NoResults
import net.aholbrook.norm.TooManyResults
import net.aholbrook.norm.get
import net.aholbrook.norm.sql.result.ResultSet
import net.aholbrook.norm.sql.result.RowData

inline fun <reified T> RowData.readValue(
    field: Field,
    transform: (Any) -> T? = { it as? T },
): Result<T, ColumnMappingError> {
    runCatching {
        val value: Any? = field.get(this)

        return if (value != null) {
            runCatching {
                transform(value)
                    ?: return Err(ColumnWrongType(field, T::class, value::class))
            }.mapError { ColumnMappingError(field, it) }
        } else {
            Err(ColumnNotFound(field))
        }
    }
}

inline fun <T> ResultSet<*>.mapRows(
    transform: (RowData) -> Result<T, DbError>,
): Result<List<T>, DbError> = when (size) {
    0 -> Err(NoResults)
    else -> this.mapResult { transform(it) }
}

inline fun <T> ResultSet<*>.mapFirstRow(
    transform: (RowData) -> Result<T, DbError>,
): Result<T, DbError> = when (size) {
    0 -> Err(NoResults)
    else -> transform(this[0])
}

inline fun <T> ResultSet<*>.mapExactlyOneRow(
    transform: (RowData) -> Result<T, DbError>,
): Result<T, DbError> = when (size) {
    0 -> Err(NoResults)
    1 -> transform(this[0])
    else -> Err(TooManyResults)
}
