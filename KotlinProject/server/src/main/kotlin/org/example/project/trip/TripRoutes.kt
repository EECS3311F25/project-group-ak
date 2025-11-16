package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Trip HTTP routes.
 *
 * Pattern (same as UserRoutes):
 * - Application.configureTripRoutes(PostgresTripRepository)
 * - Route handlers call TripService for validation
 * - Then call PostgresTripRepository for actual DB operations
 *
 * Endpoints (proposal):
 *  GET    /trip/user/{userName}  -> list trips for a user
 *  GET    /trip/{id}             -> get trip by id
 *  POST   /trip                  -> create trip
 *  PUT    /trip/{id}             -> update trip
 *  DELETE /trip/{id}             -> delete trip
 */
fun Application.configureTripRoutes(tripRepository: PostgresTripRepository) {

    routing {
        route("/trip") {

            // GET /trip/user/{userName} - get all trips owned by a user
            get("/user/{userName}") {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "userName is required")
                    return@get
                }

                val trips = tripRepository.allTripsByUsername(userName)
                call.respond(HttpStatusCode.OK, trips)
            }

            // GET /trip/{id} - get a single trip by ID
            get("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@get
                }

                val trip = tripRepository.getTripById(id)
                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                } else {
                    call.respond(HttpStatusCode.OK, trip)
                }
            }

            // POST /trip - create a new trip
            post {
                val trip = try {
                    call.receive<Trip>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@post
                }

                // Validate via service
                TripService.validateTripForCreate(trip)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Trip validation failed"
                        )
                        return@post
                    }

                val created = tripRepository.addTrip(trip)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /trip/{id} - update existing trip
            put("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@put
                }

                val trip = try {
                    call.receive<Trip>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@put
                }

                // Validate via service
                TripService.validateTripForUpdate(trip)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Trip validation failed"
                        )
                        return@put
                    }

                val updated = tripRepository.updateTrip(id, trip)
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Trip updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                }
            }

            // DELETE /trip/{id} - delete a trip
            delete("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@delete
                }

                val deleted = tripRepository.deleteTripById(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                }
            }
        }
    }
}