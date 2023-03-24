package net.aholbrook.norm.repositories.sql.test

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.models.Lot
import net.aholbrook.norm.models.LotSpace
import net.aholbrook.norm.models.Space
import net.aholbrook.norm.repositories.MutableRepository
import net.aholbrook.norm.repositories.SqlMutableRepository
import net.aholbrook.norm.repositories.SqlRepository
import net.aholbrook.norm.repositories.base.test.LotKey
import net.aholbrook.norm.repositories.base.test.LotPrimaryKey
import net.aholbrook.norm.repositories.base.test.LotRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.aliased

class LotSqlRepository(
    connection: Connection<*, *>,
    private val repository: SqlRepository<Lot> = SqlRepository(connection, Lot.table),
) : MutableRepository<Lot, LotKey> by SqlMutableRepository(repository, LotPrimaryKey),
    LotRepository {
    override suspend fun getAllWithSpaces(): Result<List<LotSpace>, DbError> = with(repository) {
        val lotFields = table.fields.values
        val spaceFields = Space.table.fields.values

        val query = """
            SELECT
                ${lotFields.joinToString { it.aliased("l") } },
                ${spaceFields.joinToString { it.aliased("s") } }
            FROM test.lot "l"
            LEFT JOIN test.space "s" ON l.id = s.lot_id
        """.trimIndent()

        val result = connection.query(query)

        return binding {
            result.rows.map { LotSpace.decode(it).bind() }
        }
    }
}
