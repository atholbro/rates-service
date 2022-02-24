package com.spothero.rates.api.model

import com.spothero.rates.api.model.serde.ApiErrorSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ApiErrorSerializer::class)
data class ApiError(val message: String)
