package com.spothero.rates.api.model

import com.spothero.rates.db.models.Rate
import kotlinx.serialization.Serializable

@Serializable
data class ApiPrice(
    val price: Int,
)

fun Rate.toPrice(): ApiPrice = ApiPrice(
    price = price,
)
