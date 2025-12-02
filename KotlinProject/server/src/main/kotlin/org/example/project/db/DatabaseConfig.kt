package org.example.project.db


data class DatabaseConfig(
    val host: String,
    val port: Int,
    val databaseName: String,
    val username: String,
    val password: String,
    val driver: String = "org.postgresql.Driver"
) {
    val jdbcUrl: String
        get() = "jdbc:postgresql://$host:$port/$databaseName"

    companion object {
        fun fromEnvironment(): DatabaseConfig {
            return DatabaseConfig(
                DBConnectionConstants.POSTGRES_DB_HOST,
                DBConnectionConstants.POSTGRES_DB_PORT,
                DBConnectionConstants.POSTGRES_DB_NAME,
                DBConnectionConstants.POSTGRES_USER_NAME,
                DBConnectionConstants.POSTGRES_USER_PSW
            )
        }
    }
}