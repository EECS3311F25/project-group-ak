package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.project.db.configureDatabases
import org.example.project.db.configureRouting
import org.example.project.db.configureSerialization

const val SERVER_PORT: Int = 8080

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

// App entry point: only wires serialization, DB, and routing.
// All concrete routes (user/trip/event) are set up inside db.configureRouting().
fun Application.module() {
    // JSON (kotlinx) content negotiation
    configureSerialization()

    // PostgreSQL + Flyway migrations
    configureDatabases()

    // Register all HTTP routes
    configureRouting()
}