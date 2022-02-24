package net.aholbrook.norm.repositories.base.test

import net.aholbrook.norm.DuoKey
import net.aholbrook.norm.models.CompoundKeyTest
import net.aholbrook.norm.repositories.base.MutableRepository

typealias CompoundKeyTestKey = DuoKey<Int, Int>

interface CompoundKeyTestRepository :
    MutableRepository<CompoundKeyTest, CompoundKeyTestKey>
