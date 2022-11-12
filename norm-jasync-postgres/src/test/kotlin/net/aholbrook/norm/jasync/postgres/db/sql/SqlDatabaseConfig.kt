/* ktlint-disable filename */
package net.aholbrook.norm.jasync.postgres.db.sql

import net.aholbrook.norm.sql.SqlDatabaseConfig

fun SqlDatabaseConfig.Companion.fromEnvironment() = SqlDatabaseConfig(
    host = System.getenv("NORM_POSTGRES_HOST") ?: "localhost",
    port = (System.getenv("NORM_POSTGRES_PORT") ?: "").toShortOrNull() ?: 15432,
    database = System.getenv("NORM_POSTGRES_DB") ?: "test",
    username = System.getenv("NORM_POSTGRES_USER") ?: "test",
    password = System.getenv("NORM_POSTGRES_PASSWORD") ?: "password",
)
