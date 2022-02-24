package net.aholbrook.norm.sql

data class SqlDatabaseConfig(
    val host: String,
    val port: Short,
    val database: String,
    val username: String,
    val password: String,
) {
    companion object
}
