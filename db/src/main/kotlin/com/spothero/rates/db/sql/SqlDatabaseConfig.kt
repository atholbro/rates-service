/* ktlint-disable filename */
package com.spothero.rates.db.sql

import net.aholbrook.norm.sql.SqlDatabaseConfig

fun SqlDatabaseConfig.Companion.fromEnvironment() = SqlDatabaseConfig(
    host = System.getenv("POSTGRES_HOST") ?: "localhost",
    port = (System.getenv("POSTGRES_PORT") ?: "").toShortOrNull() ?: 15432,
    database = System.getenv("POSTGRES_DB") ?: "rates_service",
    username = System.getenv("POSTGRES_USER") ?: "rates_service",
    password = System.getenv("POSTGRES_PASSWORD") ?: "password",
)
