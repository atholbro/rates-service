package com.spothero.rates.api.model.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZoneId

class ZoneIdSerializer : KSerializer<ZoneId> {
    override val descriptor =
        PrimitiveSerialDescriptor("ZoneId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ZoneId {
        val zoneId = decoder.decodeString()

        try {
            return ZoneId.of(zoneId)
        } catch (ex: Throwable) {
            throw SerializationException("unknown ZoneId: $zoneId.", ex)
        }
    }

    override fun serialize(encoder: Encoder, value: ZoneId) {
        encoder.encodeString(value.toString())
    }
}
