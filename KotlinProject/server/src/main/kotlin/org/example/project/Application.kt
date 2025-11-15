package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import org.example.project.db.configureDatabases
import org.example.project.db.configureRouting

import org.example.project.user.PostgresUserRepository
import org.example.project.user.configureUserSerialization

import org.example.project.trip.PostgresTripRepository
import org.example.project.trip.TripService
import org.example.project.trip.configureTripRoutes

import org.example.project.event.PostgresEventRepository
import org.example.project.event.EventService
import org.example.project.event.configureEventRoutes

const val SERVER_PORT: Int = 8080

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

// references:
// - https://ktor.io/docs/full-stack-development-with-kotlin-multiplatform.html#create-server
// - https://ktor.io/docs/server-integrate-database.html#add-routes

fun Application.module() {

    val userRepository = PostgresUserRepository()
    val tripRepository = PostgresTripRepository()
    val eventRepository = PostgresEventRepository()


    val tripService = TripService(tripRepository)
    val eventService = EventService(eventRepository)


    configureUserSerialization(userRepository)
    configureTripRoutes(tripService)
    configureEventRoutes(eventService)

    configureDatabases()
    configureRouting()
}