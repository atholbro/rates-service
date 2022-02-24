package net.aholbrook.norm

import net.aholbrook.norm.repositories.base.test.CompoundKeyTestRepository
import net.aholbrook.norm.repositories.base.test.LotRepository

interface TestDb {
    val lots: LotRepository
    val compoundKeyTest: CompoundKeyTestRepository

    suspend fun <T> inTransaction(block: suspend TestDb.() -> T): T
}
