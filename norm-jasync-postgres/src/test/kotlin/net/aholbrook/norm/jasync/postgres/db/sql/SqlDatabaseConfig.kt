package net.aholbrook.norm.jasync.postgres.db.sql

import net.aholbrook.norm.sql.SqlDatabaseConfig

fun SqlDatabaseConfig.Companion.fromEnvironment() = SqlDatabaseConfig(
    host = System.getenv("POSTGRES_HOST") ?: "localhost",
    port = (System.getenv("POSTGRES_PORT") ?: "").toShortOrNull() ?: 15432,
    database = System.getenv("POSTGRES_DB") ?: "test",
    username = System.getenv("POSTGRES_USER") ?: "test",
    password = System.getenv("POSTGRES_PASSWORD") ?: "password",
)
