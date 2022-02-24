package com.spothero.rates.db

import com.spothero.rates.db.models.Rate
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

object MockRatesData {
    val start = OffsetDateTime.parse("2022-02-25T10:03:05-05:00")
    val end = start.plusHours(1)

    val group: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    val rate = Rate(
        rateGroup = group,
        day = DayOfWeek.FRIDAY,
        start = LocalTime.of(8, 30, 0),
        end = LocalTime.of(17, 25, 31),
        timeZone = ZoneId.of("America/Toronto"),
        price = 1000,
    )

    val rate2 = rate.copy(day = DayOfWeek.WEDNESDAY)
}
