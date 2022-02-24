package net.aholbrook.norm.repositories.base

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key

interface MutableRepository<Entity, PrimaryKey : Key> : KeyedRepository<Entity, PrimaryKey> {
    suspend fun insert(entity: Entity): Result<Entity, DbError>
    suspend fun insertAll(entities: List<Entity>): Result<List<Entity>, DbError>

    suspend fun update(entity: Entity): Result<Entity, DbError>

    suspend fun delete(pk: PrimaryKey): Result<Entity, DbError>

    suspend fun clear(): Result<Long, DbError>
}
