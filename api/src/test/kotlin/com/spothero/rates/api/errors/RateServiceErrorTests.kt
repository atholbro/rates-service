package com.spothero.rates.api.errors

import com.spothero.rates.core.DatabaseError
import com.spothero.rates.core.EndBeforeStart
import com.spothero.rates.core.UnavailableRate
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import net.aholbrook.norm.NoResults
import org.junit.jupiter.api.Test

class RateServiceErrorTests {
    @Test
    fun `UnavailableRate is correctly mapped to 404`() {
        with(RateServiceError(UnavailableRate)) {
            this.toString() shouldBe "unavailable"
            this.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `EndBeforeStart is correctly mapped to HTTP 400`() {
        with(RateServiceError(EndBeforeStart)) {
            this.toString() shouldBe "unavailable"
            this.status shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun `Generic errors aare correctly mapped to HTTP 500`() {
        with(RateServiceError(DatabaseError(NoResults))) {
            this.toString() shouldBe NoResults.toString()
            this.status shouldBe HttpStatusCode.InternalServerError
        }
    }
}
