package org.example.project.event

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Event HTTP routes.
 *
 * Pattern (same as UserRoutes and TripRoutes):
 * - Application.configureEventRoutes(PostgresEventRepository)
 * - Route handlers call EventService for validation
 * - Then call PostgresEventRepository for actual DB operations
 *
 * Endpoints (proposal):
 *  GET    /event/trip/{tripId}  -> list events for a trip
 *  GET    /event/{id}           -> get event by id
 *  POST   /event                -> create event
 *  PUT    /event/{id}           -> update event
 *  DELETE /event/{id}           -> delete event
 */
fun Application.configureEventRoutes(eventRepository: PostgresEventRepository) {

    routing {
        route("/event") {

            // GET /event/trip/{tripId} - get all events of a trip
            get("/trip/{tripId}") {
                val tripIdParam = call.parameters["tripId"]
                val tripId = tripIdParam?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing tripId")
                    return@get
                }

                val events = eventRepository.allEventsByTripId(tripId)
                call.respond(HttpStatusCode.OK, events)
            }

            // GET /event/{id} - get a single event by ID
            get("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing event id")
                    return@get
                }

                val event = eventRepository.getEventById(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                } else {
                    call.respond(HttpStatusCode.OK, event)
                }
            }

            // POST /event - create a new event
            post {
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

                val created = eventRepository.addEvent(event)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /event/{id} - update existing event
            put("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
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
                    call.respond(HttpStatusCode.OK, "Event updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                }
            }

            // DELETE /event/{id} - delete an event
            delete("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
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