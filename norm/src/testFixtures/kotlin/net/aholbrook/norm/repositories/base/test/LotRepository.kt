package net.aholbrook.norm.repositories.base.test

import com.github.michaelbull.result.Result
import net.aholbrook.norm.DbError
import net.aholbrook.norm.SingleKey
import net.aholbrook.norm.models.Lot
import net.aholbrook.norm.models.LotSpace
import net.aholbrook.norm.repositories.base.MutableRepository
import java.util.UUID

typealias LotKey = SingleKey<UUID>

interface LotRepository : MutableRepository<Lot, LotKey> {
    suspend fun getAllWithSpaces(): Result<List<LotSpace>, DbError>
}
