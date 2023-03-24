package net.aholbrook.norm.repositories

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key
import net.aholbrook.norm.Table

interface Repository<E> {
    val table: Table<E>
}

interface PrimaryKey<E, PK : Key> {
    fun pk(entity: E): PK
}

interface GetAll<E> {
    suspend fun getAll(): Result<List<E>, DbError>
}

interface GetByPrimaryKey<E, PK : Key> {
    suspend fun get(pk: PK): Result<E, DbError>
}

interface Insert<E> {
    suspend fun insert(entity: E): Result<E, DbError>
    suspend fun insertAll(entities: List<E>): Result<List<E>, DbError>
}

interface Update<E> {
    suspend fun update(entity: E): Result<E, DbError>
}

interface Delete<E, PK : Key> {
    suspend fun delete(pk: PK): Result<E, DbError>
}

interface Clear {
    suspend fun clear(): Result<Long, DbError>
}

interface MutableRepository<E, PK : Key> :
    PrimaryKey<E, PK>,
    GetAll<E>,
    GetByPrimaryKey<E, PK>,
    Insert<E>,
    Update<E>,
    Delete<E, PK>,
    Clear
