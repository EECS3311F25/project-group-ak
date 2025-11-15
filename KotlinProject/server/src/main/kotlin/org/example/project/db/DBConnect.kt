package org.example.project.db

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database


//  TODO: store DB config (to prevent app leaks)
//  reference: https://www.jetbrains.com/help/exposed/working-with-database.html#connecting-to-a-database

fun Application.configureDatabases() {

    val config = DatabaseConfig.fromEnvironment()
    Migrations.runMigrations(config)

    val postgresDB = Database.connect(
        config.jdbcUrl,
        driver = config.driver,
        user = config.username,
        password = config.password
    )

    log.info("DB connected successfully: ${config.databaseName}")

    //  TODO: replace deprecated method
    environment.monitor.subscribe(ApplicationStopped) {
        log.info("Closing DB connection")
    }
}