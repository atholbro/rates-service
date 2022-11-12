package net.aholbrook.norm.repositories.sql

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key
import net.aholbrook.norm.Table
import net.aholbrook.norm.mapDatabaseExceptions
import net.aholbrook.norm.repositories.base.KeyedRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.mapExactlyOneRow
import net.aholbrook.norm.sql.pkWhereClause

abstract class SqlKeyedRepository<Entity, PrimaryKey : Key>(
    connection: Connection<*, *>,
    table: Table<Entity>,
) : SqlRepository<Entity>(connection, table), KeyedRepository<Entity, PrimaryKey> {
    protected open val getQuery get() = """
        $getAllQuery
        WHERE ${pkWhereClause(table.primaryKeyFields.values)}
    """.trimIndent()

    override suspend fun get(pk: PrimaryKey): Result<Entity, DbError> =
        mapDatabaseExceptions {
            connection.query(
                sql = getQuery,
                values = pk.toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }
}
