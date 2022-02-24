package net.aholbrook.norm.repositories.base

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError

interface Repository<Entity> {
    suspend fun getAll(): Result<List<Entity>, DbError>
}
