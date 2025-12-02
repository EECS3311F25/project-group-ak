package org.example.project.db

import org.flywaydb.core.Flyway


/**
 * Handles database migrations using Flyway.
 * Runs SQL migration scripts from resources/db/migration/
 */
object Migrations {
    /**
     * Run database migrations.
     * @param config Database configuration containing connection details
     */
    fun runMigrations(config: DatabaseConfig) {
        val flyway = Flyway.configure()
            .dataSource(config.jdbcUrl, config.username, config.password)
            .baselineOnMigrate(true)
            .locations("classpath:db_create-tables")
            .load()

        try {
            /**
             * Include flyway.repair() to wipe existing migration file checksums
             */
            flyway.repair()
            flyway.migrate()
            println("Database migrations completed successfully")
        } catch (e: Exception) {
            println("Database migration failed: ${e.message}")
            throw e
        }
    }

    /**
     * Clean and re-run all migrations (use with caution - drops all data!)
     * Useful for development/testing environments only.
     */
    fun cleanAndMigrate(config: DatabaseConfig) {
        val flyway = Flyway.configure()
            .dataSource(config.jdbcUrl, config.username, config.password)
            .locations("classpath:db_create-tables")
            .cleanDisabled(false)
            .load()

        try {
            flyway.clean()
            flyway.migrate()
            println("Database cleaned and migrated successfully")
        } catch (e: Exception) {
            println("Database clean/migration failed: ${e.message}")
            throw e
        }
    }
}