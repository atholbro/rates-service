package com.spothero.rates.api.errors

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.http.HttpStatusCode
import java.time.OffsetDateTime

class DateTimeParseError(
    val message: String,
    override val cause: Throwable,
) : HttpRequestError(HttpStatusCode.BadRequest, cause) {
    override fun toString() = "$message: ${cause.localizedMessage}"

    companion object {
        fun catch(
            message: String,
            block: () -> OffsetDateTime,
        ): Result<OffsetDateTime, DateTimeParseError> = try {
            Ok(block())
        } catch (ex: Throwable) {
            Err(DateTimeParseError(message, ex))
        }
    }
}
