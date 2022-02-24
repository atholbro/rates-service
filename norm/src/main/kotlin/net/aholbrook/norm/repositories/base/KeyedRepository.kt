package net.aholbrook.norm.repositories.base

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError
import net.aholbrook.norm.Key

interface KeyedRepository<Entity, PrimaryKey : Key> : Repository<Entity> {
    fun pk(entity: Entity): PrimaryKey

    suspend fun get(pk: PrimaryKey): Result<Entity, DbError>
}
