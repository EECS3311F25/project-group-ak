package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import org.example.project.config.AIConfig
import org.example.project.db.configureDatabases
import org.example.project.db.configureRouting
import org.example.project.db.configureSerialization
import org.example.project.repository.TripRepositoryImpl
import org.example.project.routes.configureAISummaryRoutes
import org.example.project.service.AISummaryService
import org.example.project.trip.PostgresTripRepository

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
    
    //  Serializable to JSON - see Serialization.kt
    configureSerialization()

    //  DB configuration and migration - see DatabaseConnect.kt and Migration.kt
    configureDatabases()

    // Initialize AI configuration and service
    val aiConfig = AIConfig()
    val aiSummaryService = AISummaryService(aiConfig)

    // Initialize trip repository adapter for AI summary (bridges database and AI summary interfaces)
    val tripRepository = TripRepositoryImpl()

    // register shutdown hook to close HTTP client
    monitor.subscribe(ApplicationStopped) {
        aiSummaryService.close()
    }

    //  Register all HTTP routes - see Routing.kt (includes database routes)
    configureRouting()
    
    // Register AI summary routes (your feature)
    configureAISummaryRoutes(aiSummaryService, tripRepository)
            }
