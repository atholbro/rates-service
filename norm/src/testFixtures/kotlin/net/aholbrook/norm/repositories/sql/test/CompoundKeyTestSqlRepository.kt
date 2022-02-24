package net.aholbrook.norm.repositories.sql.test

import net.aholbrook.norm.models.CompoundKeyTest
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestKey
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestRepository
import net.aholbrook.norm.repositories.sql.SqlMutableRepository
import net.aholbrook.norm.sql.Connection

class CompoundKeyTestSqlRepository(
    connection: Connection<*, *>,
) : SqlMutableRepository<CompoundKeyTest, CompoundKeyTestKey>(connection, CompoundKeyTest.table),
    CompoundKeyTestRepository {
    override fun pk(entity: CompoundKeyTest) =
        CompoundKeyTestKey(entity.part1, entity.part2)
}
