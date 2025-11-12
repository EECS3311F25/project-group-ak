package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import org.example.project.userModel.PostgresUserRepository


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
    val repository = PostgresUserRepository()

    //  Serialization.kt
    configureSerialization(repository)
    //  Database.kt
    configureDatabases()
    //  Routing.kt
    configureRouting()
}