package com.spothero.rates.api.model.serde

import com.spothero.rates.api.model.StartEndTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

class StartEndTimeSerializer : KSerializer<StartEndTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StartEndTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): StartEndTime {
        val value = decoder.decodeString()

        return try {
            val (start, end) = value.split("-")

            StartEndTime(
                start = LocalTime.parse(start, formatter),
                end = LocalTime.parse(end, formatter),
            )
        } catch (ex: Throwable) {
            throw SerializationException("Unable to deserialize start/end time: $value.", ex)
        }
    }

    override fun serialize(encoder: Encoder, value: StartEndTime) {
        try {
            encoder.encodeString("${formatter.format(value.start)}-${formatter.format(value.end)}")
        } catch (ex: Throwable) {
            throw SerializationException("Unable to encode start/end time: $value.", ex)
        }
    }
}
