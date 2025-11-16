package org.example.project.db

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}