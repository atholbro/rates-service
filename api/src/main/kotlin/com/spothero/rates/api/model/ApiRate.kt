package com.spothero.rates.api.model

import com.spothero.rates.api.model.serde.ZoneIdSerializer
import com.spothero.rates.db.models.Rate
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.util.UUID

@Serializable
data class ApiRates(
    val rates: List<ApiRate>,
) {
    companion object
}

@Serializable
data class ApiRate(
    val days: Days,
    val times: StartEndTime,
    @Serializable(with = ZoneIdSerializer::class)
    val tz: ZoneId,
    val price: Int,
)

fun ApiRates.Companion.fromService(rates: List<Rate>) = ApiRates(
    rates = rates.groupBy { it.rateGroup }
        .map { entry ->
            val first = entry.value.first()
            ApiRate(
                days = entry.value.map { it.day }.toDays(),
                times = StartEndTime(first.start, first.end),
                tz = first.timeZone,
                price = first.price,
            )
        }
)

fun ApiRates.toService(): List<Rate> = rates.flatMap { it.toService() }

fun ApiRate.toService(): List<Rate> {
    val group = UUID.randomUUID()

    return days.map { dow ->
        Rate(
            rateGroup = group,
            day = dow,
            start = times.start,
            end = times.end,
            timeZone = tz,
            price = price
        )
    }
}
