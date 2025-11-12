package org.example.project.config

/**
 * Configuration class for database connection settings.
 * Loads settings from environment variables to avoid hardcoding credentials.
 */
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
        /**
         * Load database configuration from environment variables.
         * Falls back to default values for local development.
         */
        fun fromEnvironment(): DatabaseConfig {
            return DatabaseConfig(
                host = System.getenv("DB_HOST") ?: "localhost",
                port = System.getenv("DB_PORT")?.toIntOrNull() ?: 5432,
                databaseName = System.getenv("DB_NAME") ?: "navi_db",
                username = System.getenv("DB_USER") ?: "postgres",
                password = System.getenv("DB_PASSWORD") ?: "postgres"
            )
        }
    }
}
