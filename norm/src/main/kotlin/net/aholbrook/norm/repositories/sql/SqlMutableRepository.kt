package net.aholbrook.norm.repositories.sql

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.binding.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key
import net.aholbrook.norm.Table
import net.aholbrook.norm.mapDatabaseExceptions
import net.aholbrook.norm.repositories.base.MutableRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.escaped
import net.aholbrook.norm.sql.mapExactlyOneRow
import net.aholbrook.norm.sql.pkWhereClause

abstract class SqlMutableRepository<Entity, PrimaryKey : Key>(
    connection: Connection<*, *>,
    table: Table<Entity>,
) : SqlKeyedRepository<Entity, PrimaryKey>(connection, table),
    MutableRepository<Entity, PrimaryKey> {
    protected open val insertQuery: String by lazy {
        val fields = table.fields.values.filter { !it.primaryKey }
        """
            INSERT INTO ${table.fqn} (${fields.joinToString { it.escaped() }})
                VALUES (${(1..fields.size).joinToString { "?" }})
                RETURNING ${table.fields.values.joinToString { it.escaped() }};
        """.trimIndent()
    }

    protected open val updateQuery: String by lazy {
        val fields = table.fields.values.filter { !it.primaryKey }

        """
            UPDATE ${table.fqn}
            SET ${fields.joinToString { "${it.escaped()} = ?" }}
            WHERE ${pkWhereClause(table.primaryKeyFields.values)}
            RETURNING ${table.fields.values.joinToString { it.escaped() }};
        """.trimIndent()
    }

    protected open val deleteQuery: String by lazy {
        val pkFields = table.primaryKeyFields.values
        """
            DELETE
            FROM ${table.fqn}
            WHERE ${pkWhereClause(pkFields)}
            RETURNING ${table.fields.values.joinToString { it.escaped() }};
        """.trimIndent()
    }

    protected open val clearQuery: String by lazy {
        "DELETE FROM ${table.fqn};"
    }

    override suspend fun insert(entity: Entity): Result<Entity, DbError> =
        mapDatabaseExceptions {
            connection.sendPreparedStatement(
                query = insertQuery,
                values = table.entityEncoder(entity),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }

    override suspend fun insertAll(entities: List<Entity>): Result<List<Entity>, DbError> {
        return connection.inTransaction { db ->
            return@inTransaction binding<List<Entity>, DbError> {
                val results = mutableListOf<Entity>()

                entities.forEach { entity ->
                    results.add(
                        mapDatabaseExceptions {
                            db.sendPreparedStatement(
                                query = insertQuery,
                                values = table.entityEncoder(entity),
                                release = false,
                            )
                        }
                            .andThen { result ->
                                result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
                            }
                            .bind()
                    )
                }

                results
            }
        }
    }

    override suspend fun update(entity: Entity): Result<Entity, DbError> =
        mapDatabaseExceptions {
            connection.sendPreparedStatement(
                query = updateQuery,
                values = table.entityEncoder(entity) + pk(entity).toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }

    override suspend fun delete(pk: PrimaryKey): Result<Entity, DbError> =
        mapDatabaseExceptions {
            connection.sendPreparedStatement(
                query = deleteQuery,
                values = pk.toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }

    override suspend fun clear(): Result<Long, DbError> =
        mapDatabaseExceptions {
            connection.sendPreparedStatement(
                query = clearQuery,
                values = listOf(),
                release = false,
            )
        }.andThen { result ->
            Ok(result.rowsAffected)
        }
}
