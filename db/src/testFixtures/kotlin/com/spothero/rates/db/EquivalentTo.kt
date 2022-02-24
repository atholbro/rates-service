package com.spothero.rates.db

import com.spothero.rates.db.models.Rate
import io.kotest.matchers.shouldBe

infix fun Rate.shouldBeEquivalentTo(other: Rate) {
    rateGroup shouldBe other.rateGroup
    day shouldBe other.day
    start shouldBe other.start
    end shouldBe other.end
    timeZone shouldBe other.timeZone
    price shouldBe other.price
}
