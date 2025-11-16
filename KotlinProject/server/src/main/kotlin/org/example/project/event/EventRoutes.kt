package org.example.project.event

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureEventRoutes(eventService: EventService) {
    routing {

        get("/trip/{tripId}/events") {
            val tripId = call.parameters["tripId"]?.toIntOrNull()
            if (tripId == null) {
                call.respond(HttpStatusCode.BadRequest, "tripId must be an integer")
                return@get
            }

            val events = eventService.allEventsByTripId(tripId)
            call.respond(events)
        }

        route("/event") {

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "id must be an integer")
                    return@get
                }

                val event = eventService.getEventById(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                } else {
                    call.respond(event)
                }
            }

            post {
                try {
                    val event = call.receive<Event>()
                    eventService.addEvent(event)
                        .onSuccess { created ->
                            call.respond(HttpStatusCode.Created, created)
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                error.message ?: "Failed to create event"
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
                    val event = call.receive<Event>()
                    eventService.updateEvent(id, event)
                        .onSuccess {
                            call.respond(HttpStatusCode.OK)
                        }
                        .onFailure { error ->
                            val status = if (error is NoSuchElementException)
                                HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                            call.respond(status, error.message ?: "Failed to update event")
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

                eventService.deleteEventById(id)
                    .onSuccess {
                        call.respond(HttpStatusCode.NoContent)
                    }
                    .onFailure { error ->
                        val status = if (error is NoSuchElementException)
                            HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                        call.respond(status, error.message ?: "Failed to delete event")
                    }
            }
        }
    }
}