package com.spothero.rates.api.util

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.spothero.rates.api.model.ApiRates
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream

sealed class RatesError(val cause: Throwable) {
    override fun toString(): String = cause.localizedMessage

    companion object
}
class RatesGenericError(exception: Throwable) : RatesError(exception)
class RatesIoError(exception: IOException) : RatesError(exception)
class RatesJsonError(exception: SerializationException) : RatesError(exception)

fun RatesError.Companion.mapThrowable(ex: Throwable): RatesError = when (ex) {
    is IOException -> RatesIoError(ex)
    is SerializationException -> RatesJsonError(ex)
    else -> RatesGenericError(ex)
}

fun parseRates(inputStream: InputStream): Result<ApiRates, RatesError> =
    runCatching { inputStream.bufferedReader().readText() }
        .mapError { RatesError.mapThrowable(it) }
        .andThen { parseRates(it) }

fun parseRates(json: String): Result<ApiRates, RatesError> =
    runCatching { Json.decodeFromString<ApiRates>(json) }
        .mapError { RatesError.mapThrowable(it) }
