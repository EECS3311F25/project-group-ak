package org.example.project.data.api

import org.example.project.data.api.dto.BackendDuration
import org.example.project.data.api.dto.BackendEvent
import org.example.project.data.api.dto.BackendLocation
import org.example.project.data.api.dto.BackendTrip
import org.example.project.model.dataClasses.Trip as FrontendTrip
import org.example.project.model.dataClasses.Event as FrontendEvent

/**
 * Converts frontend Trip model to backend Trip model format
 * Used for sending trip data to AI summary endpoint when trip is in localCreatedTrips
 */
object TripConverter {
    fun toBackendTrip(frontendTrip: FrontendTrip): BackendTrip {
        // Convert Duration: frontend uses LocalDate/LocalTime, backend uses ISO strings
        val startDateTime = frontendTrip.duration.getStartDateTime()
        val endDateTime = frontendTrip.duration.getEndDateTime()
        val backendDuration = BackendDuration(
            start = startDateTime.toString(), // ISO format: 2025-07-01T09:00:00
            end = endDateTime.toString()
        )
        
        // Convert Events
        val backendEvents = frontendTrip.events.map { frontendEvent ->
            val eventStart = frontendEvent.duration.getStartDateTime()
            val eventEnd = frontendEvent.duration.getEndDateTime()
            BackendEvent(
                id = frontendEvent.id,
                title = frontendEvent.title,
                description = frontendEvent.description.takeIf { it.isNotBlank() },
                location = null, // Frontend uses String location, backend uses Location object - skip for now
                duration = BackendDuration(
                    start = eventStart.toString(),
                    end = eventEnd.toString()
                )
            )
        }
        
        // Convert Users: Frontend has List<User>, backend needs List<String>
        val userNames = frontendTrip.users.map { it.name }
        
        // Get owner (first user or empty string)
        val owner = userNames.firstOrNull() ?: ""
        
        return BackendTrip(
            id = frontendTrip.id,
            name = frontendTrip.title, // Frontend uses "title", backend uses "name"
            owner = owner,
            users = userNames,
            events = backendEvents,
            duration = backendDuration
        )
    }
}

