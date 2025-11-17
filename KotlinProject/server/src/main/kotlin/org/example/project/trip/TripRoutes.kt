package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Trip HTTP routes.
 *
 * Flow (same pattern as user):
 *  - Application.module() creates PostgresTripRepository
 *  - Application.configureTripSerialization(tripRepository)
 *  - Handlers:
 *      - validate input via TripService
 *      - call PostgresTripRepository for DB CRUD
 *
 * IMPORTANT:
 *  - All Trip operations are scoped under a specific user:
 *      /user/{userName}/trip/...
 *
 * Endpoints:
 *  GET    /user/{userName}/trip          -> list trips for that user
 *  GET    /user/{userName}/trip/{id}     -> get trip by id (for that user)
 *  POST   /user/{userName}/trip          -> create trip for that user
 *  PUT    /user/{userName}/trip/{id}     -> update trip
 *  DELETE /user/{userName}/trip/{id}     -> delete trip
 */ 
fun Application.configureTripSerialization(tripRepository: PostgresTripRepository) {

    routing {
        // All trip routes are nested under a specific user
        route("/user/{userName}/trip") {

            // GET /user/{userName}/trip  -> all trips of this user
            get {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@get
                }

                // Repository currently ignores userName in DB layer,
                // but route design is already user-scoped for future extension.
                val trips = tripRepository.allTripsByUsername(userName)
                call.respond(HttpStatusCode.OK, trips)
            }

            // GET /user/{userName}/trip/{id}  -> single trip by id
            get("/{id}") {
                val userName = call.parameters["userName"]
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@get
                }
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@get
                }

                val trip = tripRepository.getTripById(id)
                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                } else {
                    // NOTE: we are not yet checking user ownership at DB level.
                    call.respond(HttpStatusCode.OK, trip)
                }
            }

            // POST /user/{userName}/trip  -> create new trip for this user
            post {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@post
                }

                val trip = try {
                    call.receive<Trip>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@post
                }

                // Validate via service (no DB access here)
                TripService.validateTripForCreate(trip)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Trip validation failed"
                        )
                        return@post
                    }

                // TODO: Later, attach this trip to userName in DB schema (add user FK)
                val created = tripRepository.addTrip(trip)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /user/{userName}/trip/{id}  -> update existing trip
            put("/{id}") {
                val userName = call.parameters["userName"]
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@put
                }
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
                    // NOTE: still no DB-level user ownership check
                    call.respond(HttpStatusCode.OK, "Trip updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                }
            }

            // DELETE /user/{userName}/trip/{id}  -> delete trip
            delete("/{id}") {
                val userName = call.parameters["userName"]
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@delete
                }
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