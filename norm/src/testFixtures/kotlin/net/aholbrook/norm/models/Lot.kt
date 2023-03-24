package net.aholbrook.norm.models

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Table
import net.aholbrook.norm.namedField
import net.aholbrook.norm.sql.readValue
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.table
import java.util.UUID

data class Lot(
    val id: UUID = DEFAULT_UUID_FOR_INSERTS,
    var address: String
) {
    companion object {
        val table: Table<Lot> = table {
            schema = "test"
            name = "lot"

            entityEncoder = Companion::encode
            entityDecoder = Companion::decode

            field<UUID>("id", primaryKey = true)
            field<String>("address")
        }

        private fun encode(lot: Lot): List<Any?> = with(lot) {
            listOf(address)
        }

        private fun decode(row: RowData, prefix: String = ""): Result<Lot, DbError> = binding {
            Lot(
                id = row.readValue<UUID>(namedField("${prefix}id")).bind(),
                address = row.readValue<String>(namedField("${prefix}address")).bind()
            )
        }
    }
}
