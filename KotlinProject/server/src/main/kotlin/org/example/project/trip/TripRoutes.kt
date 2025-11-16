package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureTripRoutes(tripService: TripService) {
    routing {
        route("/trip") {

            get("/user/{userName}") {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@get
                }

                val trips = tripService.allTripsByUsername(userName)
                call.respond(trips)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip")
                    return@get
                }

                val trip = tripService.getTripById(id)
                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                } else {
                    call.respond(trip)
                }
            }

            post {
                try {
                    val trip = call.receive<Trip>()
                    tripService.addTrip(trip)
                        .onSuccess { created ->
                            call.respond(HttpStatusCode.Created, created)
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                error.message ?: "Failed to create trip"
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid request body: ${e.message}"
                    )
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "id must be an integer")
                    return@put
                }

                try {
                    val trip = call.receive<Trip>()
                    tripService.updateTrip(id, trip)
                        .onSuccess {
                            call.respond(HttpStatusCode.OK)
                        }
                        .onFailure { error ->
                            val status = if (error is NoSuchElementException)
                                HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                            call.respond(status, error.message ?: "Failed to update trip")
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid request body: ${e.message}"
                    )
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "id must be an integer")
                    return@delete
                }

                tripService.deleteTripById(id)
                    .onSuccess {
                        call.respond(HttpStatusCode.NoContent)
                    }
                    .onFailure { error ->
                        val status = if (error is NoSuchElementException)
                            HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                        call.respond(status, error.message ?: "Failed to delete trip")
                    }
            }
        }
    }
}