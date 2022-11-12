package com.spothero.rates.db.models

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Table
import net.aholbrook.norm.namedField
import net.aholbrook.norm.sql.readValue
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.table
import net.aholbrook.norm.util.asEnum
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

data class Rate(
    val id: UUID = DEFAULT_UUID_FOR_INSERTS,
    var rateGroup: UUID,
    var day: DayOfWeek,
    var start: LocalTime,
    var end: LocalTime,
    var timeZone: ZoneId,
    var price: Int,
) {
    private fun encode(): List<Any?> = listOf(
        rateGroup,
        day.asEnum(),
        start,
        end,
        timeZone.toString(),
        price,
    )

    companion object {
        val table: Table<Rate> = table {
            name = "rates"

            entityEncoder = Rate::encode
            entityDecoder = Companion::decode

            field<UUID>("id", primaryKey = true)
            field<UUID>("rate_group")
            field<DayOfWeek>("day")
            field<LocalTime>("start")
            field<LocalTime>("end")
            field<ZoneId>("timezone")
            field<Int>("price")
        }

        private fun decode(row: RowData, prefix: String = ""): Result<Rate, DbError> = binding {
            Rate(
                id = row.readValue<UUID>(namedField("${prefix}id")).bind(),
                rateGroup = row.readValue<UUID>(namedField("${prefix}rate_group")).bind(),
                day = row.readValue(namedField("${prefix}day")) { DayOfWeek.valueOf(it as String) }.bind(),
                start = row.readValue<LocalTime>(namedField("${prefix}start")).bind(),
                end = row.readValue<LocalTime>(namedField("${prefix}end")).bind(),
                timeZone = row.readValue(namedField("${prefix}timezone")) { ZoneId.of(it as String) }.bind(),
                price = row.readValue<Int>(namedField("${prefix}price")).bind(),
            )
        }
    }
}
