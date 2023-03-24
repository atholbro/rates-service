package net.aholbrook.norm.repositories

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.binding.binding
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key
import net.aholbrook.norm.Table
import net.aholbrook.norm.mapDatabaseExceptions
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.mapExactlyOneRow
import net.aholbrook.norm.sql.mapRows
import net.aholbrook.norm.sql.sqlDelete
import net.aholbrook.norm.sql.sqlGetAll
import net.aholbrook.norm.sql.sqlGetByPrimaryKey
import net.aholbrook.norm.sql.sqlInsert
import net.aholbrook.norm.sql.sqlUpdate

fun <E> sqlClear(table: Table<E>): String = "DELETE FROM ${table.fqn};"

data class SqlRepository<E>(
    val connection: Connection<*, *>,
    override val table: Table<E>,
) : Repository<E>

class SqlGetAll<E>(
    private val repository: SqlRepository<E>,
    private val query: String = sqlGetAll(repository.table),
) : GetAll<E> {
    override suspend fun getAll(): Result<List<E>, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.query(
                sql = query,
                release = false,
            )
        }.andThen { result ->
            result.rows.mapRows { table.entityDecoder(it, "") }
        }
    }
}

class SqlGetByPrimaryKey<E, PK : Key>(
    private val repository: SqlRepository<E>,
    private val query: String = sqlGetByPrimaryKey(repository.table),
) : GetByPrimaryKey<E, PK> {
    override suspend fun get(pk: PK): Result<E, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.query(
                sql = query,
                values = pk.toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }
    }
}

class SqlInsert<E>(
    private val repository: SqlRepository<E>,
    private val query: String = sqlInsert(repository.table),
) : Insert<E> {
    override suspend fun insert(entity: E): Result<E, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.query(
                sql = query,
                values = table.entityEncoder(entity),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }
    }

    override suspend fun insertAll(entities: List<E>): Result<List<E>, DbError> = with(repository) {
        connection.inTransaction { db ->
            return@inTransaction binding<List<E>, DbError> {
                val results = mutableListOf<E>()

                entities.forEach { entity ->
                    results.add(
                        mapDatabaseExceptions {
                            db.query(
                                sql = query,
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
}

class SqlUpdate<E>(
    private val repository: SqlRepository<E>,
    private val primaryKey: PrimaryKey<E, *>,
    private val query: String = sqlUpdate(repository.table),
) : Update<E> {
    override suspend fun update(entity: E): Result<E, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.query(
                sql = query,
                values = table.entityEncoder(entity) + primaryKey.pk(entity).toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }
    }
}

class SqlDelete<E, PK : Key>(
    private val repository: SqlRepository<E>,
    private val query: String = sqlDelete(repository.table),
) : Delete<E, PK> {
    override suspend fun delete(pk: PK): Result<E, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.query(
                sql = query,
                values = pk.toList(),
                release = false,
            )
        }.andThen { result ->
            result.rows.mapExactlyOneRow { table.entityDecoder(it, "") }
        }
    }
}

class SqlClear(
    private val repository: SqlRepository<*>,
    private val query: String = sqlClear(repository.table),
) : Clear {
    override suspend fun clear(): Result<Long, DbError> = with(repository) {
        mapDatabaseExceptions {
            connection.update(
                sql = query,
                values = listOf(),
                release = false,
            )
        }.andThen { result ->
            Ok(result)
        }
    }
}

class SqlMutableRepository<E, PK : Key>(
    repository: SqlRepository<E>,
    primaryKey: PrimaryKey<E, PK>,
) : MutableRepository<E, PK>,
    PrimaryKey<E, PK> by primaryKey,
    GetAll<E> by SqlGetAll(repository),
    GetByPrimaryKey<E, PK> by SqlGetByPrimaryKey(repository),
    Insert<E> by SqlInsert(repository),
    Update<E> by SqlUpdate(repository, primaryKey),
    Delete<E, PK> by SqlDelete(repository),
    Clear by SqlClear(repository)
