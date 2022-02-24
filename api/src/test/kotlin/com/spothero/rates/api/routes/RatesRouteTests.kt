package com.spothero.rates.api.routes

import com.spothero.rates.api.TestApplication
import com.spothero.rates.api.module
import com.spothero.rates.db.MockRatesData
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import net.aholbrook.norm.NoResults
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

const val RATES = """{"rates":[{"days":"fri","times":"0830-1725","tz":"America/Toronto","price":1000}]}"""

class RatesRouteTests {
    @BeforeEach
    fun beforeEach() {
        TestApplication.reset()
    }

    @Test
    fun `get empty rates`(): Unit = withTestApplication(Application::module) {
        handleRequest(HttpMethod.Get, "/rates").apply {
            response.status() shouldBe HttpStatusCode.OK
            response.contentType().contentType shouldBe ContentType.Application.Json.contentType
            response.content shouldBe """{"rates":[]}"""
        }
    }

    @Test
    fun `get rates`(): Unit = withTestApplication(Application::module) {
        // inject rate
        with (TestApplication.instance.mockRatesRepository) {
            rates[MockRatesData.rate.id] = MockRatesData.rate
        }

        handleRequest(HttpMethod.Get, "/rates").apply {
            response.status() shouldBe HttpStatusCode.OK
            response.contentType().contentType shouldBe ContentType.Application.Json.contentType
            response.content shouldBe RATES
        }
    }

    @Test
    fun `get handles errors correctly`(): Unit = withTestApplication(Application::module) {
        // inject rate
        with (TestApplication.instance.mockRatesRepository) {
            getAllError = NoResults
        }

        handleRequest(HttpMethod.Get, "/rates").apply {
            response.status() shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `replace rates`(): Unit = withTestApplication(Application::module) {
        with (handleRequest(HttpMethod.Put, "/rates") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(RATES)
        }) {
            response.status() shouldBe HttpStatusCode.OK
            response.contentType().contentType shouldBe ContentType.Application.Json.contentType
            response.content shouldBe RATES
        }
    }

    @Test
    fun `replace rates handles errors correctly`(): Unit = withTestApplication(Application::module) {
        with (handleRequest(HttpMethod.Put, "/rates") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{invalidjson}""")
        }) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }
}
