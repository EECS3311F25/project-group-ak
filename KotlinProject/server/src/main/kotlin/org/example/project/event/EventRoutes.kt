package org.example.project.event

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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
 *      /user/{userName}/trip/{tripId}/event/...
 *
 * Endpoints:
 *  GET    /user/{userName}/trip/{tripId}/event          -> list events for a given trip
 *  GET    /user/{userName}/trip/{tripId}/event/{id}     -> get event by id
 *  POST   /user/{userName}/trip/{tripId}/event          -> create event for that trip
 *  PUT    /user/{userName}/trip/{tripId}/event/{id}     -> update event
 *  DELETE /user/{userName}/trip/{tripId}/event/{id}     -> delete event
 */
 
fun Application.configureEventSerialization(eventRepository: PostgresEventRepository) {

    routing {
        // All events are under a specific user + trip
        route("/user/{userName}/trip/{tripId}/event") {

            // GET /user/{userName}/trip/{tripId}/event  -> all events for this trip
            get {
                val userName = call.parameters["userName"]
                val tripId = call.parameters["tripId"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@get
                }
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@get
                }

                // NOTE: repository only filters by tripId for now
                val events = eventRepository.allEventsByTripId(tripId)
                call.respond(HttpStatusCode.OK, events)
            }

            // GET /user/{userName}/trip/{tripId}/event/{id}  -> single event
            get("/{id}") {
                val userName = call.parameters["userName"]
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@get
                }
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@get
                }
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing event id")
                    return@get
                }

                val event = eventRepository.getEventById(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                } else {
                    // NOTE: we are not yet checking that this event actually belongs to tripId
                    call.respond(HttpStatusCode.OK, event)
                }
            }

            // POST /user/{userName}/trip/{tripId}/event  -> create new event for this trip
            post {
                val userName = call.parameters["userName"]
                val tripId = call.parameters["tripId"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@post
                }
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@post
                }

                val event = try {
                    call.receive<Event>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@post
                }

                // Validate via service
                EventService.validateEventForCreate(event)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Event validation failed"
                        )
                        return@post
                    }

                // TODO: in PostgresEventRepository.addEvent, link the event to this tripId
                val created = eventRepository.addEvent(event)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /user/{userName}/trip/{tripId}/event/{id}  -> update event
            put("/{id}") {
                val userName = call.parameters["userName"]
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@put
                }
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@put
                }
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing event id")
                    return@put
                }

                val event = try {
                    call.receive<Event>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@put
                }

                // Validate via service
                EventService.validateEventForUpdate(event)
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Event validation failed"
                        )
                        return@put
                    }

                val updated = eventRepository.updateEvent(id, event)
                if (updated) {
                    // NOTE: still no DB-level check that event belongs to this tripId
                    call.respond(HttpStatusCode.OK, "Event updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                }
            }

            // DELETE /user/{userName}/trip/{tripId}/event/{id}  -> delete event
            delete("/{id}") {
                val userName = call.parameters["userName"]
                val tripId = call.parameters["tripId"]?.toIntOrNull()
                val id = call.parameters["id"]?.toIntOrNull()

                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@delete
                }
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@delete
                }
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing event id")
                    return@delete
                }

                val deleted = eventRepository.deleteEventById(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                }
            }
        }
    }
}