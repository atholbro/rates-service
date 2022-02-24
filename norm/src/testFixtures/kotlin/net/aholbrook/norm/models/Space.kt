package net.aholbrook.norm.models

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOr
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Table
import net.aholbrook.norm.namedField
import net.aholbrook.norm.sql.readValue
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.table
import java.util.UUID

data class Space(
    val id: UUID = DEFAULT_UUID_FOR_INSERTS,
    val lotId: UUID,
    val number: String?,
) {
    private fun encode(): List<Any?> = listOf(
        lotId,
        number,
    )

    companion object {
        val table: Table<Space> = table {
            schema = "test"
            name = "space"

            entityEncoder = Space::encode
            entityDecoder = Companion::decode

            field<UUID>("id", primaryKey = true)
            field<UUID>("lot_id")
            field<String?>("number")
        }

        private fun decode(row: RowData, prefix: String = ""): Result<Space, DbError> = binding {
            Space(
                id = row.readValue<UUID>(namedField("${prefix}id")).bind(),
                lotId = row.readValue<UUID>(namedField("${prefix}lot_id")).bind(),
                number = row.readValue<String?>(namedField("${prefix}number")).getOr(null),
            )
        }
    }
}
