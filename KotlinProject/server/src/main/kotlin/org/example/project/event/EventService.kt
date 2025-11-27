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

    fun validateEventForCreate(eventDto: EventCreateRequest): Result<Unit> {
        if (eventDto.eventTitle.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))

        if (eventDto.eventLocation.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))

        // Location validation
        eventDto.location?.let {
            if (it.latitude == 0.0 && it.longitude == 0.0)
                return Result.failure(IllegalArgumentException("Invalid GPS coordinates"))
        }

        return Result.success(Unit)
    }

    fun validateEventForUpdate(event: Event): Result<Unit> {
        if (event.eventTitle.isBlank())
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))

        if (event.eventLocation.isBlank())
            return Result.failure(IllegalArgumentException("Event location cannot be empty"))

        return Result.success(Unit)
    }
}