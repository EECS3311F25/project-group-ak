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

fun Application.module() {
    //  Serializable to JSON - see Serialization.kt
    configureSerialization()

    //  DB configuration and migration - see DatabaseConnect.kt and Migration.kt
    configureDatabases()

    //  Register all HTTP routes - see Routing.kt
    configureRouting()
}