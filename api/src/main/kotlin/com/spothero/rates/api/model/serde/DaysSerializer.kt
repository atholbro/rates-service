package com.spothero.rates.api.model.serde

import com.spothero.rates.api.model.Days
import com.spothero.rates.api.model.toDays
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DayOfWeek

private fun DayOfWeek.toSpothero() = when (this) {
    DayOfWeek.MONDAY -> "mon"
    DayOfWeek.TUESDAY -> "tues"
    DayOfWeek.WEDNESDAY -> "wed"
    DayOfWeek.THURSDAY -> "thurs"
    DayOfWeek.FRIDAY -> "fri"
    DayOfWeek.SATURDAY -> "sat"
    DayOfWeek.SUNDAY -> "sun"
}

fun fromSpothero(dayOfWeek: String): DayOfWeek? = when (dayOfWeek) {
    "mon" -> DayOfWeek.MONDAY
    "tues" -> DayOfWeek.TUESDAY
    "wed" -> DayOfWeek.WEDNESDAY
    "thurs" -> DayOfWeek.THURSDAY
    "fri" -> DayOfWeek.FRIDAY
    "sat" -> DayOfWeek.SATURDAY
    "sun" -> DayOfWeek.SUNDAY
    else -> null
}

class DaysSerializer : KSerializer<Days> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Days", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Days {
        return decoder.decodeString()
            .split(",")
            .map { fromSpothero(it) ?: throw SerializationException("unknown DayOfWeek: $it.") }
            .toDays()
    }

    override fun serialize(encoder: Encoder, value: Days) {
        encoder.encodeString(value.joinToString(separator = ",") { it.toSpothero() })
    }
}
