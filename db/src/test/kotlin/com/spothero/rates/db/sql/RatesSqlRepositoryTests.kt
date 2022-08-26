package com.spothero.rates.db.sql

import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import com.spothero.rates.db.MockRatesData
import com.spothero.rates.db.RatesServiceDb
import com.spothero.rates.db.models.DEFAULT_UUID_FOR_INSERTS
import com.spothero.rates.db.models.Rate
import com.spothero.rates.db.rolledBackTransaction
import com.spothero.rates.db.shouldBeEquivalentTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.aholbrook.norm.NoResults
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

data class TestData(
    val db: RatesServiceDb,
    val rate: Rate,
) {
    companion object {
        fun use(block: suspend (TestData) -> Unit): Unit = rolledBackTransaction { db ->
            db.rates.clear()
            val actual = db.rates.insert(MockRatesData.rate).expect { "Unable to insert." }
            block(TestData(db, actual))
        }
    }
}

class RatesServiceSqlDbTest {
    @Nested
    @DisplayName("CRUD Tests")
    inner class CrudTests {
        @Test
        fun insert(): Unit = rolledBackTransaction { db ->
            val actual = db.rates.insert(MockRatesData.rate)
            with(actual.expect { "Unable to insert row into database." }) {
                id shouldNotBe DEFAULT_UUID_FOR_INSERTS
                this shouldBeEquivalentTo MockRatesData.rate
            }
        }

        @Test
        fun insertAll(): Unit = rolledBackTransaction { db ->
            val expected = listOf(MockRatesData.rate, MockRatesData.rate2)
            val actual = db.rates.insertAll(expected)
            with(actual.expect { "Unable to insert rows into database." }) {
                this.size shouldBe expected.size
                this[0].id shouldNotBe DEFAULT_UUID_FOR_INSERTS
                this[0] shouldBeEquivalentTo expected[0]
                this[1].id shouldNotBe DEFAULT_UUID_FOR_INSERTS
                this[1] shouldBeEquivalentTo expected[1]
            }
        }

        @Test
        fun get(): Unit = TestData.use { data ->
            val actual = with(data.db.rates) {
                get(pk(data.rate))
            }.expect { "Failed to get rate from DB." }

            actual shouldBeEquivalentTo MockRatesData.rate
        }

        @Test
        fun update(): Unit = TestData.use { data ->
            val expected = data.rate.copy(day = DayOfWeek.MONDAY)
            val actual = data.db.rates.update(expected).expect { "Unable to update row in database." }

            with(actual) {
                id shouldNotBe DEFAULT_UUID_FOR_INSERTS
                actual shouldBeEquivalentTo expected
            }
        }

        @Test
        fun delete(): Unit = TestData.use { data ->
            val actual = with(data.db.rates) {
                delete(pk(data.rate))
            }.expect { "Unable to delete Rate." }

            actual shouldBeEquivalentTo data.rate
        }
    }

    @Nested
    @DisplayName("Rate Lookup Tests")
    inner class RateLookupTests {
        @Test
        fun `can find single rate`(): Unit = TestData.use { data ->
            val actual = data.db.rates.findRates(MockRatesData.start, MockRatesData.end)
                .expect { "Unable to locate FRIDAY rate." }

            actual.size shouldBe 1
            actual[0] shouldBeEquivalentTo data.rate
        }

        @Test
        fun `can find multiple rates`(): Unit = TestData.use { data ->
            // Add another rate that starts and ends within the existing rate.
            val other = with(data.rate) {
                copy(
                    start = start.plusHours(1),
                    end = end.minusHours(1),
                )
            }
            data.db.rates.insert(other).expect { "Unable to insert additional rate." }

            var actual = data.db.rates.findRates(MockRatesData.start, MockRatesData.end)
                .expect { "Unable to locate FRIDAY rates." }

            actual.size shouldBe 2
            actual = actual.sortedBy { it.start }
            actual[0] shouldBeEquivalentTo data.rate
            actual[1] shouldBeEquivalentTo other
        }

        @Test
        fun `finds no rates when expected`(): Unit = TestData.use { data ->
            val actual = data.db.rates.findRates(
                MockRatesData.start.minusDays(1),
                MockRatesData.end.minusDays(1),
            ).expectError {
                "Was able to locate a rate on THURSDAY when no rate was expected."
            }

            actual shouldBe NoResults
        }
    }
}
