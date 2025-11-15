package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.project.db.configureDatabases
import org.example.project.db.configureRouting
import org.example.project.db.configureSerialization


fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

//  references:
//  -   https://ktor.io/docs/full-stack-development-with-kotlin-multiplatform.html#create-server
//  -   https://ktor.io/docs/server-integrate-database.html#add-routes

fun Application.module() {
    //  Serialization.kt
    configureSerialization()

    //  DatabaseConnect.kt
    configureDatabases()

    //  Routing.kt
    configureRouting()
}