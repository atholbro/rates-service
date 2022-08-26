package com.spothero.rates.db.repositories.sql._public

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.spothero.rates.db.models.Rate
import com.spothero.rates.db.repositories.base._public.RateKey
import com.spothero.rates.db.repositories.base._public.RatesRepository
import net.aholbrook.norm.DbError
import net.aholbrook.norm.mapDatabaseExceptions
import net.aholbrook.norm.repositories.sql.SqlMutableRepository
import net.aholbrook.norm.sql.Connection
import net.aholbrook.norm.sql.mapRows
import org.intellij.lang.annotations.Language
import java.time.OffsetDateTime

class RatesSqlRepository(
    connection: Connection<*, *>,
) : SqlMutableRepository<Rate, RateKey>(connection, Rate.table),
    RatesRepository {
    override fun pk(entity: Rate): RateKey = RateKey(entity.id)

    override suspend fun findRates(
        start: OffsetDateTime,
        end: OffsetDateTime,
    ): Result<List<Rate>, DbError> = mapDatabaseExceptions {
        /*
            Finds all rates which contain the specified start/end time.

            This query first creates a CTE to preform date/time calculations on our stored rates.

            CTE "input":
            Inside the CTE we first sub query select all rates, and append the given start + end
            timestamp, converted to the timezone for that row. So our sub select results in the following:
            (id, rate_group, day, start, end, timestamp, price, query_start, query_end)
            query_start: the given "start" timestamp, converted to the "timezone" for the row
            query_end: the given "end" timestamp, converted to the "timezone" for the row
            where "start" and "end" are the arguments to this findRates function.

            Then the query calculates midnight for the query_start/query_end times, and stores these as
            query_start_midnight/query_end_midnight respectively. Next we calculate the day of week "dow"
            for the query_start (extract gives us an int where 0=sun, 6=sat, so the dow function converts
            this to our "day_of_week" enum type).

            Finally, the CTE filters these results to return only rates for the correct day of the week.

            Query:
            Now with the CTE our query becomes quite simple as we only have to further filter the CTE
            results using the available columns.

            The start/end time for the rate is calculated from midnight on the query timestamps then
            compared with the query timestamp.
        */
        @Language("sql")
        val sql = """
            WITH input (id, rate_group, day, start, "end", timezone, price, query_start, query_end) AS (
                SELECT sub.id, sub.rate_group, sub.day, sub.start, sub."end", sub.timezone, sub.price, sub.query_start, sub.query_end,
                       date_trunc('DAY', sub.query_start) "query_start_midnight",
                       date_trunc('DAY', sub.query_end) "query_end_midnight",
                       dow(extract(DOW FROM sub.query_start)) "dow"
                FROM (
                    SELECT id, rate_group, day, start, "end", timezone, price,
                        ? AT TIME ZONE timezone "query_start",
                        ? AT TIME ZONE timezone "query_end"
                    FROM rates
                ) "sub"
                WHERE day = dow(extract(DOW FROM sub.query_start))
            )
            SELECT id, rate_group, day, start, "end", "timezone", "price"
            FROM input
            WHERE input.query_start_midnight + input.start <= input.query_start
                AND input.query_end_midnight + input."end" >= input.query_end;
        """.trimIndent()

        connection.sendPreparedStatement(
            query = sql,
            values = listOf(start, end),
            release = false,
        )
    }.andThen { result ->
        result.rows.mapRows { table.entityDecoder(it, "") }
    }
}
