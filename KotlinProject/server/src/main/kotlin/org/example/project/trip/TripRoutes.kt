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
 * Endpoints:
 *  /user/{userId}/trip...
     *  GET    /                                -> list trips associated with userId
     *  GET    /{id}                            -> get trip by its id (if the associated User requested access)
     *  POST   /                                -> create trip (if the associated User requested creation)
     *  PUT    /{id}                            -> update trip (if the associated User requested updating)
     *  DELETE /{id}                            -> delete trip (if the associated User requested deletion)
     */

fun Application.configureTripSerialization(tripRepository: PostgresTripRepository) {

    routing {
        //  All trip routes are prepended by the following
        route("/user/{userId}/trip") {

            //  GET /user/{userId}/trip  -> all trips associated with userId
            get {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@get
                }

                //  Repository currently ignores userId in DB layer,
                //  but route design is already user-scoped for future extension.
                val trips = tripRepository.allTripsByUserId(userId)
                call.respond(HttpStatusCode.OK, trips)
            }

            //  GET /user/{userId}/trip/{id}  -> single trip by id
            get("/{id}") {
                // TODO: depending on frontend interaction, decide whether getTripById should require associated User ID
//                val userId = call.parameters["userId"]?.toIntOrNull()
//                if (userId == null) {
//                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
//                    return@get
//                }
                val tripId = call.parameters["id"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip id")
                    return@get
                }

                val trip = tripRepository.getTripById(tripId)
                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                } else {
                    call.respond(HttpStatusCode.OK, trip)
                }
            }

            // POST /user/{userId}/trip  -> create new trip for user of ID user_id
            post {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@post
                }
                val trip = try {
                    call.receive<Trip>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@post
                }

                //  Validate via service (no DB access here)
                TripService.validateTripForCreate(trip)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Trip validation failed"
                        )
                        return@post
                    }

                val created = tripRepository.addTrip(userId, trip)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /user/{userId}/trip/{id}  -> update existing trip
            put("/{id}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@put
                }
                val trip = try {
                    call.receive<Trip>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@put
                }
                val tripId = call.parameters["id"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@put
                }

                //  Validate via service
                TripService.validateTripForUpdate(trip)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Trip validation failed"
                        )
                        return@put
                    }

                val updated = tripRepository.updateTrip(userId, tripId, trip)
                if (updated) {
                    // NOTE: still no DB-level user ownership check
                    call.respond(HttpStatusCode.OK, "Trip updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                }
            }

            // DELETE /user/{userName}/trip/{id}  -> delete trip
            delete("/{id}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@delete
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@delete
                }

                val deleted = tripRepository.deleteTrip(userId, tripId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Trip not found")
                }
            }
        }
    }
}