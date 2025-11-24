package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.config.AIConfig
import org.example.project.repository.TripRepositoryImpl
import org.example.project.routes.configureAISummaryRoutes
import org.example.project.service.AISummaryService
import org.example.project.trip.tripRoutes

const val SERVER_PORT: Int = 8080

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait=true)
}

// fun fun_name.module() - this is extension function on Ktor Application class
// "Add functionality (routing, plugins, config) to the Ktor Application object."

fun Application.module() {
    // Install CORS
    install(CORS) {
        anyHost() // Allow requests from any host (for development)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
    
    // Install ContentNegotiation for JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
        }

    // Initialize AI configuration and service
    val aiConfig = AIConfig()
    val aiSummaryService = AISummaryService(aiConfig)

    // Initialize trip repository (uses PostgreSQL when database is set up)
    val tripRepository = TripRepositoryImpl()

    // register shutdown hook to close HTTP client
    monitor.subscribe(ApplicationStopped) {
        aiSummaryService.close()
    }
    
    routing {
        get("/") { call.respondText("Ktor: ${Greeting().greet()}") }
        get("/health") { call.respondText("Healthy") }

        tripRoutes()
        configureAISummaryRoutes(aiSummaryService, tripRepository)
        mockApiRoutes()
    }
}
