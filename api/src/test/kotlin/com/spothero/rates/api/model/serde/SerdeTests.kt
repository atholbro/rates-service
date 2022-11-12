package com.spothero.rates.api.model.serde

import com.spothero.rates.api.model.ApiError
import com.spothero.rates.api.model.ApiRates
import com.spothero.rates.api.model.fromService
import com.spothero.rates.api.model.toPrice
import com.spothero.rates.api.model.toService
import com.spothero.rates.db.models.Rate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

/* ktlint-disable max-line-length */
const val RATE_JSON = """{"rates":[{"days":"thurs,tues,fri,mon,sun,wed,sat","times":"0830-1725","tz":"America/Toronto","price":132}]}"""
/* ktlint-enable max-line-length */

val rate = Rate(
    rateGroup = UUID.fromString("00000000-0000-0000-0000-000000000001"),
    day = DayOfWeek.MONDAY,
    start = LocalTime.of(8, 30),
    end = LocalTime.of(17, 25),
    timeZone = ZoneId.of("America/Toronto"),
    price = 132,
)

val rates = listOf(
    rate,
    rate.copy(day = DayOfWeek.TUESDAY),
    rate.copy(day = DayOfWeek.WEDNESDAY),
    rate.copy(day = DayOfWeek.THURSDAY),
    rate.copy(day = DayOfWeek.FRIDAY),
    rate.copy(day = DayOfWeek.SATURDAY),
    rate.copy(day = DayOfWeek.SUNDAY)
)

class SerdeTests {
    @Test
    fun `can serialize rates`() {
        val rates = ApiRates.fromService(rates)
        val json = Json.encodeToString(rates)

        json shouldNotBe ""
    }

    @Test
    fun `can deserialize rates`() {
        val rates = Json.decodeFromString<ApiRates>(RATE_JSON)
        rates.rates.size shouldBe 1

        with(rates.toService()[0]) {
            // order is not fixed, so can't check day
            start shouldBe rate.start
            end shouldBe rate.end
            timeZone shouldBe rate.timeZone
            price shouldBe rate.price
        }
    }

    @Test
    fun `can serialize errors`() {
        val json = Json.encodeToString(ApiError("error"))
        json shouldBe """"error""""
    }

    @Test
    fun `can serialize price`() {
        val json = Json.encodeToString(rate.toPrice())

        json shouldBe """{"price":132}"""
    }
}
