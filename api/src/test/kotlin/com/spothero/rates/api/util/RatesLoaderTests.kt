package com.spothero.rates.api.util

import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

class RatesLoaderTests {
    @Test
    fun `RatesError correctly returns wrapped message for toString`() {
        RatesGenericError(Exception("test")).toString() shouldBe "test"
        RatesIoError(IOException("test")).toString() shouldBe "test"
        RatesJsonError(SerializationException("test")).toString() shouldBe "test"
    }

    @Test
    fun `mapThrowable correctly maps errors`() {
        RatesError.mapThrowable(IOException(""))::class shouldBe RatesIoError::class
        RatesError.mapThrowable(SerializationException(""))::class shouldBe RatesJsonError::class
        RatesError.mapThrowable(IllegalArgumentException(""))::class shouldBe RatesGenericError::class
    }

    @Test
    fun `parseRates can parse json rates`() {
        val json = """{"rates":[]}"""
        val actual = parseRates(json).expect { "Expected empty rates." }
        actual.rates.size shouldBe 0
    }

    @Test
    fun `parseRates wraps json errors`() {
        val json = """{"rates":err}"""
        val actual = parseRates(json).expectError { "Expected JSON parsing error." }
        actual::class shouldBe RatesJsonError::class
    }
}
