package org.example.project.event

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

/**
 * Event HTTP routes.
 *
 * Flow:
 *  - Application.module() creates PostgresEventRepository
 *  - Application.configureEventSerialization(eventRepository)
 *  - Handlers:
 *      - validate input via EventService
 *      - call PostgresEventRepository for DB CRUD
 *
 * IMPORTANT:
 *  - All Event operations are scoped under a specific user AND trip:
 *      /user/{userId}/trip/{tripId}/event/...
 *
 * Endpoints:
 * /user/{userId}/trip/{tripId}/event...
 *  GET    /          -> list events for a given trip
 *  GET    /{id}     -> get event by id
 *  POST   /        -> create event for that trip
 *  PUT    /{id}     -> update event
 *  DELETE /{id}     -> delete event
 */

//  TODO: verify associated Trip is valid, and is associated with the correct User

fun Application.configureEventSerialization(eventRepository: PostgresEventRepository) {

    routing {
        // All events are under a specific user + trip
        route("/user/{userId}/trip/{tripId}/event") {

            //  GET /user/{userId}/trip/{tripId}/event  -> all events for this trip
            get {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@get
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip ID")
                    return@get
                }
                val events = eventRepository.allEventsByTrip(tripId)
                call.respond(HttpStatusCode.OK,
                    EventListResponse("Events retrieved for trip $tripId of user $userId", events))
            }

            //  GET /user/{userId}/trip/{tripId}/event/{id}  -> single event
            get("/{id}") {
                val userId = call.parameters["userId"]
                if (userId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@get
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip ID")
                    return@get
                }
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing event ID")
                    return@get
                }
                val event = eventRepository.getEvent(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                } else {
                    call.respond(HttpStatusCode.OK, event)
                }
            }

            //  POST /user/{userId}/trip/{tripId}/event  -> create new event for this trip
            post {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@post
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip ID")
                    return@post
                }
                try {
                    val eventDto = call.receive<EventCreateRequest>()
                    EventService.validateEventForCreate(eventDto)
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                error.message ?: "Event validation failed"
                            )
                            return@post
                        }
                    val addResult = eventRepository.addEvent(tripId, eventDto)
                    val event = addResult.getOrNull()

                    if (event != null) {
                        call.respond(
                            HttpStatusCode.Created,
                            EventRetrieveResponse("Event created successfully", event)
                        )
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Event creation failed")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@post
                }
            }

            //  PUT /user/{userId}/trip/{tripId}/event/{id}  -> update event
            put("/{id}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@put
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip ID")
                    return@put
                }
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing event ID")
                    return@put
                }
                try {
                    val event = call.receive<Event>()
                    EventService.validateEventForUpdate(event)
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                error.message ?: "Event validation failed"
                            )
                            return@put
                        }
                    eventRepository.updateEvent(id, event)
                        .onSuccess {
                            call.respond(HttpStatusCode.OK, "Event updated successfully")
                        }
                        .onFailure {
                            call.respond(HttpStatusCode.NotFound, "Event not found")
                        }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@put
                }
            }

            //  DELETE /user/{userId}/trip/{tripId}/event/{id}  -> delete event
            delete("/{id}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing user ID")
                    return@delete
                }
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing trip ID")
                    return@delete
                }
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing event ID")
                    return@delete
                }
                eventRepository.deleteEvent(id)
                    .onSuccess {
                        call.respond(HttpStatusCode.NoContent)
                    }
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound, "Event not found")
                }
            }
        }
    }
}