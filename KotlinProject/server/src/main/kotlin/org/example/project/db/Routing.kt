package org.example.project.db

import io.ktor.server.application.*
import org.example.project.event.PostgresEventRepository
import org.example.project.event.configureEventRoutes
import org.example.project.trip.PostgresTripRepository
import org.example.project.trip.configureTripRoutes
import org.example.project.user.PostgresUserRepository
import org.example.project.user.configureUserSerialization

// Central place to wire repositories into Ktor routes.
fun Application.configureRouting() {
    val userRepository = PostgresUserRepository()
    val tripRepository = PostgresTripRepository()
    val eventRepository = PostgresEventRepository()

    // User account routes
    configureUserSerialization(userRepository)

    //  Trip CRUD routes
    configureTripRoutes(tripRepository)

    //  Event CRUD routes
    configureEventRoutes(eventRepository)
}