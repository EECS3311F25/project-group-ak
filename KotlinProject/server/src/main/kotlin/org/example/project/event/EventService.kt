package org.example.project.event

/**
 * Business logic and validation utilities for Event operations.
 *
 * NOTE:
 * - Ensure Event creation/update inputs are valid before repository access.
 * - Keep service layer strict but simple.
 */
object EventService {

    /**
     * Validate input for event creation.
     */
    fun validateEventForCreate(event: EventCreateRequest): Result<Unit> {

        if (event.eventTitle.isBlank())
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))

        if (event.eventDuration == null)
            return Result.failure(IllegalArgumentException("Event duration is missing"))

        // Validate Location
        validateLocation(event.location).getOrElse { return Result.failure(it) }

        return Result.success(Unit)
    }

    /**
     * Validate input for event update.
     * Uses Event domain model (not DTO).
     */
    fun validateEventForUpdate(event: Event): Result<Unit> {

        if (event.eventTitle.isBlank())
            return Result.failure(IllegalArgumentException("Event title cannot be empty"))

        if (event.eventDuration == null)
            return Result.failure(IllegalArgumentException("Event duration is missing"))

        // Validate Location
        validateLocation(event.location).getOrElse { return Result.failure(it) }

        return Result.success(Unit)
    }

    /**
     * Location validation applies to both update & create.
     */
    private fun validateLocation(loc: org.example.project.trip.Location): Result<Unit> {
        if (loc.latitude !in -90.0..90.0)
            return Result.failure(IllegalArgumentException("Invalid latitude"))

        if (loc.longitude !in -180.0..180.0)
            return Result.failure(IllegalArgumentException("Invalid longitude"))

        return Result.success(Unit)
    }
}