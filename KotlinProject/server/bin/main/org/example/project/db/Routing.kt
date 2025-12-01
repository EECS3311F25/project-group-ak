package org.example.project.db

import io.ktor.server.application.*
import org.example.project.event.PostgresEventRepository
import org.example.project.event.configureEventSerialization
import org.example.project.trip.PostgresTripRepository
import org.example.project.trip.configureTripSerialization
import org.example.project.user.PostgresUserRepository
import org.example.project.user.configureUserSerialization

/**
 * Central place to wire repositories into Ktor routes.
 *
 * Flow:
 *  - Application.module() calls configureSerialization()
 *  - Then configureDatabases()
 *  - Then configureRouting() to register all HTTP endpoints.
 */
 
fun Application.configureRouting() {
    val userRepository = PostgresUserRepository()
    val tripRepository = PostgresTripRepository()
    val eventRepository = PostgresEventRepository()

    // User account routes
    configureUserSerialization(userRepository)

    // Trip CRUD routes
    configureTripSerialization(tripRepository)

    // Event CRUD routes
    configureEventSerialization(eventRepository)
}