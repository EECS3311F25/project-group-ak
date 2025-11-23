package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.example.project.user.UserCreateDto
import org.example.project.user.UserRetrieveResponse

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
                call.respond(HttpStatusCode.OK,
                    TripListResponse("Trips retrieved for user $userId", trips))
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
                    call.respond(
                        HttpStatusCode.OK,
                        TripRetrieveResponse("Trip retrieved successfully", trip)
                    )
                }
            }

            //  POST /user/{userId}/trip  -> create new trip for user of ID user_id
            post {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@post
                }
                try {
                    //  Validate Trip creation DTO
                    val tripDto = call.receive<TripCreateDto>()

                    val addResult = tripRepository.addTrip(userId, tripDto)
                    val trip = addResult.getOrNull()

                    if (trip != null) {
                        call.respond(
                            HttpStatusCode.Created,
                            TripRetrieveResponse("Trip created successfully", trip)
                        )
                    } else {
                        println("Trip creation failed, trip is null")
                        call.respond(HttpStatusCode.BadRequest, "Trip creation failed")
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                    return@post
                }
            }

            // PUT /user/{userId}/trip/{id}  -> update existing trip
            put("/{id}") {
                try {
                    val userId = call.parameters["userId"]?.toIntOrNull()
                    if (userId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                        return@put
                    }
                    val tripId = call.parameters["id"]?.toIntOrNull()
                    if (tripId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip ID")
                        return@put
                    }
                    val trip = call.receive<Trip>()
                    TripService.validateTripForUpdate(trip)
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                error.message ?: "Trip validation failed"
                            )
                            return@put
                        }

                    tripRepository.updateTrip(userId, tripId, trip)
                        .onSuccess {
                            call.respond(HttpStatusCode.OK, "Trip updated successfully")
                        }
                        .onFailure {
                            call.respond(HttpStatusCode.NotFound, "Trip not found")
                        }
                } catch(e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid trip payload")
                return@put
                }
            }

            //  DELETE /user/{userName}/trip/{id}  -> delete trip
            delete("/{id}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@delete
                }
                val tripId = call.parameters["id"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing trip id")
                    return@delete
                }
                tripRepository.deleteTrip(userId, tripId)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.NoContent,
                            "Trip deleted successfully"
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound,
                            "Trip does not exist"
                        )
                    }
            }
        }
    }
}