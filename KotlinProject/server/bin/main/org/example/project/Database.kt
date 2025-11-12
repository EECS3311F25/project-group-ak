package org.example.project

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.example.project.config.DatabaseConfig
import org.example.project.db.Migrations

/**
 * Backend connection using Application.configureDatabases()
 * - Loads database configuration from environment variables
 * - Runs database migrations using Flyway
 * - Connects Exposed to PostgreSQL
 * - Overall: connects backend to database
 * 
 * How everything runs together:
 * 1. Ktor calls configureDatabases() during startup
 * 2. DatabaseConfig loads connection settings from environment
 * 3. Migrations.runMigrations() creates/updates database schema
 * 4. Database.connect() establishes Exposed connection pool
 * 5. Repositories use this connection for all database operations
 */
fun Application.configureDatabases() {
    // Load database configuration from environment variables
    val config = DatabaseConfig.fromEnvironment()
    
    // Run database migrations to create/update schema
    Migrations.runMigrations(config)
    
    // Connect Exposed to PostgreSQL
    val database = Database.connect(
        url = config.jdbcUrl,
        driver = config.driver,
        user = config.username,
        password = config.password
    )
    
    log.info("Database connected successfully: ${config.databaseName}")
    
    // Register shutdown hook to close connection
    environment.monitor.subscribe(ApplicationStopped) {
        log.info("Closing database connection")
        // Exposed handles connection pool cleanup automatically
    }
}