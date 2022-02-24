package net.aholbrook.norm.models

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOr
import net.aholbrook.norm.DbError
import net.aholbrook.norm.sql.result.RowData

data class LotSpace(
    val lot: Lot,
    val space: Space?,
) {
    companion object {
        fun decode(
            row: RowData,
            lotPrefix: String = "l_",
            spacePrefix: String = "s_",
        ): Result<LotSpace, DbError> = binding {
            LotSpace(
                lot = Lot.table.entityDecoder(row, lotPrefix).bind(),
                space = Space.table.entityDecoder(row, spacePrefix).getOr(null),
            )
        }
    }
}
