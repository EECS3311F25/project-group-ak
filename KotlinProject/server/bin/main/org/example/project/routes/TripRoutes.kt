package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.Trip
import org.example.project.dto.ApiResponse
import org.example.project.dto.CreateTripRequest
import org.example.project.dto.TripResponse
import org.example.project.dto.UpdateTripRequest
import org.example.project.service.TripService

/**
 * Configures trip-related routes.
 */
fun Route.tripRoutes(tripService: TripService) {
    route("/trips") {
        
        // GET /trips - Get all trips
        get {
            try {
                val trips = tripService.getAllTrips()
                val tripResponses = trips.map {
                    TripResponse(
                        tripID = it.tripID,
                        tripTitle = it.tripTitle,
                        tripLocation = it.tripLocation,
                        tripStartDate = it.tripStartDate,
                        tripEndDate = it.tripEndDate
                    )
                }
                call.respond(HttpStatusCode.OK, tripResponses)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to fetch trips: ${e.message}")
                )
            }
        }
        
        // GET /trips/{id} - Get trip by ID
        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, "Invalid trip ID")
                    )
                    return@get
                }
                
                val trip = tripService.getTripById(id)
                if (trip != null) {
                    val response = TripResponse(
                        tripID = trip.tripID,
                        tripTitle = trip.tripTitle,
                        tripLocation = trip.tripLocation,
                        tripStartDate = trip.tripStartDate,
                        tripEndDate = trip.tripEndDate
                    )
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, "Trip not found")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to fetch trip: ${e.message}")
                )
            }
        }
        
        // POST /trips - Create a new trip
        post {
            try {
                val request = call.receive<CreateTripRequest>()
                val trip = Trip(
                    tripID = 0, // Will be assigned by database
                    tripTitle = request.tripTitle,
                    tripLocation = request.tripLocation,
                    tripStartDate = request.tripStartDate,
                    tripEndDate = request.tripEndDate
                )
                
                tripService.createTrip(trip)
                    .onSuccess { createdTrip ->
                        val response = TripResponse(
                            tripID = createdTrip.tripID,
                            tripTitle = createdTrip.tripTitle,
                            tripLocation = createdTrip.tripLocation,
                            tripStartDate = createdTrip.tripStartDate,
                            tripEndDate = createdTrip.tripEndDate
                        )
                        call.respond(HttpStatusCode.Created, response)
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(false, error.message)
                        )
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to create trip: ${e.message}")
                )
            }
        }
        
        // PUT /trips/{id} - Update trip
        put("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, "Invalid trip ID")
                    )
                    return@put
                }
                
                val request = call.receive<UpdateTripRequest>()
                val trip = Trip(
                    tripID = id,
                    tripTitle = request.tripTitle,
                    tripLocation = request.tripLocation,
                    tripStartDate = request.tripStartDate,
                    tripEndDate = request.tripEndDate
                )
                
                tripService.updateTrip(id, trip)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(true, "Trip updated successfully")
                        )
                    }
                    .onFailure { error ->
                        val statusCode = if (error is NoSuchElementException) {
                            HttpStatusCode.NotFound
                        } else {
                            HttpStatusCode.BadRequest
                        }
                        call.respond(statusCode, ApiResponse(false, error.message))
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to update trip: ${e.message}")
                )
            }
        }
        
        // DELETE /trips/{id} - Delete trip
        delete("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, "Invalid trip ID")
                    )
                    return@delete
                }
                
                tripService.deleteTrip(id)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(true, "Trip deleted successfully")
                        )
                    }
                    .onFailure { error ->
                        val statusCode = if (error is NoSuchElementException) {
                            HttpStatusCode.NotFound
                        } else {
                            HttpStatusCode.InternalServerError
                        }
                        call.respond(statusCode, ApiResponse(false, error.message))
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to delete trip: ${e.message}")
                )
            }
        }
    }
}

