package com.spothero.rates.api.model

import com.spothero.rates.api.model.serde.StartEndTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable(with = StartEndTimeSerializer::class)
data class StartEndTime(
    val start: LocalTime,
    val end: LocalTime
)
