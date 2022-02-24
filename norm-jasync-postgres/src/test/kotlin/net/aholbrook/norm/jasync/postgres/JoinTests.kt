package net.aholbrook.norm.jasync.postgres

import com.github.michaelbull.result.expect
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.aholbrook.norm.jasync.postgres.db.sql.rolledBackTransaction
import net.aholbrook.norm.repositories.base.test.CompoundKeyTestKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class JoinTests {
    @Test
    fun `can join lot and space`() = rolledBackTransaction { db ->
        val actual = db.lots.getAllWithSpaces().expect { "error getting all with spaces" }

        actual.size shouldNotBe 0
    }

    @Test
    fun `can get with compound key`() = rolledBackTransaction { db ->
        val actual = db.compoundKeyTest.get(CompoundKeyTestKey(1, 3))
            .expect { "Expected result for key (1, 3)." }

        actual.`val` shouldBe "1-3!"
    }
}
