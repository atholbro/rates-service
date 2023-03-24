package net.aholbrook.norm.models

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Table
import net.aholbrook.norm.namedField
import net.aholbrook.norm.sql.readValue
import net.aholbrook.norm.sql.result.RowData
import net.aholbrook.norm.table

data class CompoundKeyTest(
    val part1: Int,
    val part2: Int,
    var `val`: String,
) {
    companion object {
        val table: Table<CompoundKeyTest> = table {
            schema = "test"
            name = "compound_key_test"

            entityEncoder = Companion::encode
            entityDecoder = Companion::decode

            field<Int>("part1", primaryKey = true)
            field<Int>("part2", primaryKey = true)
            field<String>("val")
        }

        private fun encode(compoundKeyTest: CompoundKeyTest): List<Any?> = with (compoundKeyTest) {
            listOf(`val`)
        }

        private fun decode(row: RowData, prefix: String = ""): Result<CompoundKeyTest, DbError> = binding {
            CompoundKeyTest(
                part1 = row.readValue<Int>(namedField("${prefix}part1")).bind(),
                part2 = row.readValue<Int>(namedField("${prefix}part2")).bind(),
                `val` = row.readValue<String>(namedField("${prefix}val")).bind(),
            )
        }
    }
}
