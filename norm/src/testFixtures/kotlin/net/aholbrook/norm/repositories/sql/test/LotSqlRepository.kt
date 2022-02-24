package net.aholbrook.norm.repositories.sql.test

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.models.Lot
import net.aholbrook.norm.models.LotSpace
import net.aholbrook.norm.models.Space
import net.aholbrook.norm.repositories.base.test.LotKey
import net.aholbrook.norm.repositories.base.test.LotRepository
import net.aholbrook.norm.repositories.sql.SqlMutableRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.aliased

class LotSqlRepository(
    connection: Connection<*, *>,
) : SqlMutableRepository<Lot, LotKey>(connection, Lot.table),
    LotRepository {
    override fun pk(entity: Lot) = LotKey(entity.id)

    override suspend fun getAllWithSpaces(): Result<List<LotSpace>, DbError> {
        val lotFields = table.fields.values
        val spaceFields = Space.table.fields.values

        val query = """
            SELECT
                ${lotFields.joinToString { it.aliased("l") } },
                ${spaceFields.joinToString { it.aliased("s") } }
            FROM test.lot "l"
            LEFT JOIN test.space "s" ON l.id = s.lot_id
        """.trimIndent()

        val result = connection.sendPreparedStatement(query)

        return binding {
            result.rows.map {
                LotSpace.decode(it).bind()
            }
        }
    }
}
