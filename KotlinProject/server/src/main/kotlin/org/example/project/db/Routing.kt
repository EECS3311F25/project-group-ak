package org.example.project.db

import io.ktor.server.application.*
import org.example.project.event.PostgresEventRepository
import org.example.project.event.configureEventSerialization
//  import org.example.project.event.configureEventSerialization
import org.example.project.trip.PostgresTripRepository
//  import org.example.project.trip.configureTripSerialization
import org.example.project.user.PostgresUserRepository
import org.example.project.user.configureUserSerialization

//  reference: https://ktor.io/docs/server-integrate-database.html#switch-repo
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Routing.kt


fun Application.configureRouting() {
    val userRepository = PostgresUserRepository()
    val tripRepository = PostgresTripRepository()
    val eventRepository = PostgresEventRepository()

    //  User account serializable CRUD routes
    configureUserSerialization(userRepository)

    //  Trip CRUD serializable CRUD routes
    configureTripSerialization(tripRepository)

    //  Event CRUD serializable CRUD routes
    configureEventSerialization(eventRepository)
}