package com.spothero.rates.api.errors

import io.ktor.http.HttpStatusCode

sealed class HttpRequestError(
    val status: HttpStatusCode,
    open val cause: Any,
)
