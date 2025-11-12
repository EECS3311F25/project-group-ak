package org.example.project

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.project.repository.TripRepositoryImpl
import org.example.project.routes.tripRoutes
import org.example.project.routes.userRoutes
import org.example.project.service.TripService
import org.example.project.service.UserService
import org.example.project.userModel.PostgresUserRepository

/**
 * Configures all application routes.
 * Initializes repositories and services, then registers route handlers.
 */
fun Application.configureRouting() {
    // Initialize repositories
    val userRepository = PostgresUserRepository()
    val tripRepository = TripRepositoryImpl()
    
    // Initialize services with repositories
    val userService = UserService(userRepository)
    val tripService = TripService(tripRepository)
    
    // Configure routing
    routing {
        // Health check endpoint
        get("/health") {
            call.respond(mapOf("status" to "healthy"))
        }
        
        // Register route modules
        userRoutes(userService)
        tripRoutes(tripService)
    }
}