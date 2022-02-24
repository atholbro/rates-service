package com.spothero.rates.api.model.serde

import com.spothero.rates.api.model.ApiError
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ApiErrorSerializer : KSerializer<ApiError> {
    override val descriptor =
        PrimitiveSerialDescriptor("ApiError", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ApiError {
        return ApiError(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ApiError) {
        encoder.encodeString(value.message)
    }
}
