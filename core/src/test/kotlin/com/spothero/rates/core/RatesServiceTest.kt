package com.spothero.rates.core

import com.github.michaelbull.result.expect
import com.github.michaelbull.result.expectError
import com.spothero.rates.db.MockRatesData
import com.spothero.rates.db.mock.RatesServiceMockDb
import com.spothero.rates.db.repositories.mock.RatesMockRepository
import com.spothero.rates.db.shouldBeEquivalentTo
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import net.aholbrook.norm.NoResults
import net.aholbrook.norm.TooManyResults
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class RatesServiceTest {
    private val db = RatesServiceMockDb()
    private val service = RatesService(db)

    private val mockRepo: RatesMockRepository get() =
        db.rates as RatesMockRepository

    @BeforeEach
    fun beforeEach() {
        mockRepo.reset()
    }

    @Nested
    @DisplayName("error toString tests")
    inner class ErrorToStringTests {
        @Test
        fun `DatabaseError toString returns wrapped message`() {
            val error = DatabaseError(NoResults)

            error.toString() shouldBe "No results"
        }

        @Test
        fun `EndBeforeStart results in 'unavailable'`() {
            EndBeforeStart.toString() shouldBe "unavailable"
        }

        @Test
        fun `UnavailableRate results in 'unavailable'`() {
            UnavailableRate.toString() shouldBe "unavailable"
        }
    }

    @Nested
    @DisplayName("getAll tests")
    inner class GetAllTests {
        @Test
        fun `getAll with no results`(): Unit = runBlocking {
            val actual = service.getAll()
                .expect { "Expected an empty list." }

            actual.size shouldBe 0
        }

        @Test
        fun `getAll with result`(): Unit = runBlocking {
            val expected = MockRatesData.rate
            db.rates.insert(expected)

            val actual = service.getAll()
                .expect { "Expected a list of one rate." }

            actual.size shouldBe 1
            actual[0] shouldBeEquivalentTo expected
        }

        @Test
        fun `getAll correctly maps database errors`(): Unit = runBlocking {
            mockRepo.getAllError = TooManyResults

            val actual = service.getAll()
                .expectError { "Expected TooManyResults error." }

            (actual is DatabaseError) shouldBe true
            (actual as DatabaseError).error shouldBe TooManyResults
        }
    }

    @Nested
    @DisplayName("replaceAll tests")
    inner class ReplaceAllTests {
        @Test
        fun `replaceAll can replace rates`(): Unit = runBlocking {
            mockRepo.insert(MockRatesData.rate)

            val actual = service.replaceAll(listOf(MockRatesData.rate2))
                .expect { "Expected result to replaceAll." }

            actual.size shouldBe 1
            actual[0] shouldBeEquivalentTo MockRatesData.rate2
        }

        @Test
        fun `replaceAll correctly maps db errors from clear call`(): Unit = runBlocking {
            mockRepo.clearError = NoResults

            val actual = service.replaceAll(listOf(MockRatesData.rate))
                .expectError { "Expected NoResults error." }

            (actual is DatabaseError) shouldBe true
            (actual as DatabaseError).error shouldBe NoResults
        }

        @Test
        fun `replaceAll correctly maps db errors from insertAll call`(): Unit = runBlocking {
            mockRepo.insertAllError = NoResults

            val actual = service.replaceAll(listOf(MockRatesData.rate))
                .expectError { "Expected NoResults error." }

            (actual is DatabaseError) shouldBe true
            (actual as DatabaseError).error shouldBe NoResults
        }
    }

    @Nested
    @DisplayName("lookup tests")
    inner class LookupTests {
        @Test
        fun `can lookup rates`(): Unit = runBlocking {
            val expected = MockRatesData.rate
            db.rates.insert(expected)

            val actual = service.lookup(MockRatesData.start, MockRatesData.end)
                .expect { "Expected single rate result." }

            actual shouldBeEquivalentTo expected
        }

        @Test
        fun `correctly handles end before start`(): Unit = runBlocking {
            val actual = service.lookup(MockRatesData.end, MockRatesData.start)
                .expectError { "Expected a EndBeforeStart error." }

            (actual is EndBeforeStart) shouldBe true
        }

        @Test
        fun `correctly handles multiple day span`(): Unit = runBlocking {
            val actual = service.lookup(MockRatesData.start, MockRatesData.end.plusDays(1))
                .expectError { "Expected a UnavailableRate error." }

            (actual is UnavailableRate) shouldBe true
        }

        @Test
        fun `correctly maps multiple results to UnavailableRate`(): Unit = runBlocking {
            db.rates.insertAll(listOf(MockRatesData.rate, MockRatesData.rate))

            val actual = service.lookup(MockRatesData.start, MockRatesData.end)
                .expectError { "Expected a UnavailableRate error." }

            (actual is UnavailableRate) shouldBe true
        }

        @Test
        fun `correctly maps database errors`(): Unit = runBlocking {
            mockRepo.findRatesError = TooManyResults

            val actual = service.lookup(MockRatesData.start, MockRatesData.end)
                .expectError { "Expected a database error." }

            (actual is DatabaseError) shouldBe true
            (actual as DatabaseError).error shouldBe TooManyResults
        }

        @Test
        fun `correctly maps NoResults to UnavailableRate`(): Unit = runBlocking {
            val actual = service.lookup(MockRatesData.start, MockRatesData.end)
                .expectError { "Expected a UnavailableRate error." }

            (actual is UnavailableRate) shouldBe true
        }
    }
}
