package net.aholbrook.norm.repositories.sql.test

import net.aholbrook.norm.models.CompoundKeyTest
import net.aholbrook.norm.repositories.MutableRepository
import net.aholbrook.norm.repositories.SqlMutableRepository
import net.aholbrook.norm.repositories.SqlRepository
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestKey
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestPrimaryKey
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestRepository
import net.aholbrook.norm.sql.Connection

private typealias PrimaryKey = CompoundKeyTestPrimaryKey

class CompoundKeyTestSqlRepository(
    connection: Connection<*, *>,
    private val repository: SqlRepository<CompoundKeyTest> = SqlRepository(connection, CompoundKeyTest.table),
) : MutableRepository<CompoundKeyTest, CompoundKeyTestKey> by SqlMutableRepository(repository, PrimaryKey),
    CompoundKeyTestRepository
