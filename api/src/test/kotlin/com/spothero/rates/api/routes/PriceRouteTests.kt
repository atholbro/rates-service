package com.spothero.rates.api.routes

import com.spothero.rates.api.TestApplication
import com.spothero.rates.api.module
import com.spothero.rates.db.MockRatesData
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter

const val PRICE = """{"price":1000}"""

private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

private val start = formatter.format(MockRatesData.start)
private val end = formatter.format(MockRatesData.end)
private val url = "/price?start=$start&end=$end"

class PriceRouteTests {
    @BeforeEach
    fun beforeEach() {
        TestApplication.reset()
    }

    @Test
    fun `get price`(): Unit = withTestApplication(Application::module) {
        // inject rate
        with(TestApplication.instance.mockRatesRepository) {
            rates[MockRatesData.rate.id] = MockRatesData.rate
        }

        handleRequest(HttpMethod.Get, url).apply {
            response.status() shouldBe HttpStatusCode.OK
            response.contentType().contentType shouldBe ContentType.Application.Json.contentType
            response.content shouldBe PRICE
        }
    }

    @Test
    fun `get unavailable price`(): Unit = withTestApplication(Application::module) {
        handleRequest(HttpMethod.Get, url).apply {
            response.status() shouldBe HttpStatusCode.NotFound
            response.contentType().contentType shouldBe ContentType.Application.Json.contentType
            response.content shouldBe """"unavailable""""
        }
    }
}
