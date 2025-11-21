package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.dto.ErrorResponse
import org.example.project.dto.TripSummaryResponse
import org.example.project.service.AIServiceException
import org.example.project.service.AISummaryService
import org.example.project.trip.TripRepositoryMock
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.configureAISummaryRoutes(aiSummaryService: AISummaryService) {
    val logger = LoggerFactory.getLogger("AISummaryRoutes")

    routing {
        route("/api/trips") {
            post("/{tripId}/summary") {
                val tripId = call.parameters["tripId"]

                if (tripId.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            error = "BAD_REQUEST",
                            message = "Trip ID is required"
                        )
                    )
                    return@post
                }

                try {
                    val trip = TripRepositoryMock.getById(tripId)
                    if (trip == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(
                                error = "NOT_FOUND",
                                message = "Trip with ID $tripId not found"
                            )
                        )
                        return@post
                    }

                    val summary = aiSummaryService.generateSummary(trip)
                    val response = TripSummaryResponse(
                        summary = summary,
                        generatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )

                    call.respond(HttpStatusCode.OK, response)
                    logger.info("Successfully generated summary for trip: $tripId")
                } catch (e: AIServiceException) {
                    logger.error("AI service error for trip $tripId: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        ErrorResponse(
                            error = "AI_SERVICE_ERROR",
                            message = e.message ?: "Failed to generate summary"
                        )
                    )
                } catch (e: Exception) {
                    logger.error("Unexpected error generating summary for trip $tripId: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(
                            error = "INTERNAL_ERROR",
                            message = "An unexpected error occurred: ${e.message}"
                        )
                    )
                }
            }
        }
    }
}

