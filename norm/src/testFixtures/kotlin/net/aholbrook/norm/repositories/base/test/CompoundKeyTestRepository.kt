package net.aholbrook.norm.repositories.base.test

import net.aholbrook.norm.DuoKey
import net.aholbrook.norm.models.CompoundKeyTest
import net.aholbrook.norm.repositories.Clear
import net.aholbrook.norm.repositories.Delete
import net.aholbrook.norm.repositories.GetAll
import net.aholbrook.norm.repositories.GetByPrimaryKey
import net.aholbrook.norm.repositories.Insert
import net.aholbrook.norm.repositories.PrimaryKey
import net.aholbrook.norm.repositories.Update

typealias CompoundKeyTestKey = DuoKey<Int, Int>

object CompoundKeyTestPrimaryKey : PrimaryKey<CompoundKeyTest, CompoundKeyTestKey> {
    override fun pk(entity: CompoundKeyTest): CompoundKeyTestKey = CompoundKeyTestKey(entity.part1, entity.part2)
}

interface CompoundKeyTestRepository :
    PrimaryKey<CompoundKeyTest, CompoundKeyTestKey>,
    GetAll<CompoundKeyTest>,
    GetByPrimaryKey<CompoundKeyTest, CompoundKeyTestKey>,
    Insert<CompoundKeyTest>,
    Update<CompoundKeyTest>,
    Delete<CompoundKeyTest, CompoundKeyTestKey>,
    Clear
