package org.example.project.data.remote

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json { 
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
    
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}
