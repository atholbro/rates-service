package com.spothero.rates.api.model

import com.spothero.rates.api.model.serde.DaysSerializer
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable(with = DaysSerializer::class)
class Days : HashSet<DayOfWeek>()

fun Iterable<DayOfWeek>.toDays(): Days = toCollection(Days())
