package org.example.project.event

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureEventRoutes(eventService: EventService) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/events") {

            // GET /events/trip/{tripId}
            get("/trip/{tripId}") {
                val tripIdParam = call.parameters["tripId"]
                val tripId = tripIdParam?.toIntOrNull()
                if (tripId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid trip id")
                    return@get
                }

                val events = eventService.allEventsByTripId(tripId)
                call.respond(events)
            }

            // GET /events/{id}
            get("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event id")
                    return@get
                }

                val event = eventService.getEventById(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                } else {
                    call.respond(event)
                }
            }

            // POST /events
            post {
                val event = try {
                    call.receive<Event>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@post
                }

                val result = eventService.addEvent(event)
                result
                    .onSuccess { created ->
                        call.respond(HttpStatusCode.Created, created)
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            error.message ?: "Failed to create event"
                        )
                    }
            }

            // PUT /events/{id}
            put("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event id")
                    return@put
                }

                val event = try {
                    call.receive<Event>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event payload")
                    return@put
                }

                val result = eventService.updateEvent(id, event)
                result
                    .onSuccess {
                        call.respond(HttpStatusCode.OK)
                    }
                    .onFailure { error ->
                        when (error) {
                            is NoSuchElementException ->
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    error.message ?: "Event not found"
                                )

                            else ->
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    error.message ?: "Failed to update event"
                                )
                        }
                    }
            }

            // DELETE /events/{id}
            delete("/{id}") {
                val idParam = call.parameters["id"]
                val id = idParam?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid event id")
                    return@delete
                }

                val result = eventService.deleteEventById(id)
                result
                    .onSuccess {
                        call.respond(HttpStatusCode.NoContent)
                    }
                    .onFailure { error ->
                        when (error) {
                            is NoSuchElementException ->
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    error.message ?: "Event not found"
                                )

                            else ->
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    error.message ?: "Failed to delete event"
                                )
                        }
                    }
            }
        }
    }
}