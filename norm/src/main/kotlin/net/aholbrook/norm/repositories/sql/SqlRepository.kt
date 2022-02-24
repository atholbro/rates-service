package net.aholbrook.norm.repositories.sql

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Table
import net.aholbrook.norm.repositories.base.Repository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.fqn
import net.aholbrook.norm.sql.mapRows

abstract class SqlRepository<Entity>(
    protected val connection: Connection<*, *>,
    protected val table: Table<Entity>,
) : Repository<Entity> {
    protected val getAllQuery get() = """
        SELECT ${table.fields.values.joinToString { it.fqn() }}
        FROM ${table.fqn}
    """.trimIndent()

    override suspend fun getAll(): Result<List<Entity>, DbError> {
        val result = connection.sendPreparedStatement(
            query = getAllQuery,
            release = false,
        )

        return result.rows.mapRows { table.entityDecoder(it, "") }
    }
}
