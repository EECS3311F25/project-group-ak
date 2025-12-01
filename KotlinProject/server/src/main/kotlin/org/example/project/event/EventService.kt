package org.example.project.event


/**
 * Service layer for Event business logic.
 *
 * IMPORTANT:
 * - Routes -> EventService (validation only) -> EventRepository (PostgresEventRepository) -> database
 * - This class does NOT talk to the database directly.
 * - This class does NOT hold a reference to any repository.
 *
 * Responsibility:
 * - Validate Event data for create / update.
 * - Return Result<Unit> so routes can map it to HTTP responses.
 */
object EventService {

    /**
     * Validate event data before creating a new Event.
     */
    fun validateEventForCreate(eventDto: EventCreateRequest): Result<Unit> {
        if (eventDto.eventTitle.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))
        }
        // Description is optional, no validation needed
        if (eventDto.eventLocation.isNullOrBlank() && eventDto.locationAddress.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))
        }
        val hasLat = eventDto.locationLatitude != null
        val hasLon = eventDto.locationLongitude != null
        if (hasLat != hasLon) {
            return Result.failure(IllegalArgumentException("Both latitude and longitude are required when providing coordinates"))
        }
        //  TODO: implement verification for Duration's fields

        return Result.success(Unit)
    }

    /**
     * Validate event data before updating an existing Event.
     * (Currently same rules as create; can be customized later.)
     */
    fun validateEventForUpdate(event: Event): Result<Unit> {
        if (event.eventTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))
        }

        if (event.eventDescription.isBlank()) {
            return Result.failure(IllegalArgumentException("Event description cannot be empty"))
        }
        if (event.eventLocation.isBlank() && event.locationAddress.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))
        }
        val hasLat = event.locationLatitude != null
        val hasLon = event.locationLongitude != null
        if (hasLat != hasLon) {
            return Result.failure(IllegalArgumentException("Both latitude and longitude are required when providing coordinates"))
        }
        //  TODO: implement verification for Duration's fields

        return Result.success(Unit)
    }
}
