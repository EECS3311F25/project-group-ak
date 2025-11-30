package org.example.project.repository

import org.example.project.trip.PostgresTripRepository
import org.example.project.trip.Trip
import org.example.project.trip.TripResponse
import org.slf4j.LoggerFactory

/**
 * Adapter that bridges the AI summary's TripRepository interface with the database PostgresTripRepository.
 * 
 * Converts between:
 * - AI summary interface: getById(id: String): Trip?
 * - Database interface: getTrip(tripId: Int?): TripResponse?
 */
class TripRepositoryImpl(
    private val postgresRepository: PostgresTripRepository = PostgresTripRepository()
) : TripRepository {
    private val logger = LoggerFactory.getLogger(TripRepositoryImpl::class.java)

    override suspend fun getById(id: String): Trip? {
        val tripId = id.toIntOrNull()
        if (tripId == null) {
            logger.warn("Invalid trip ID format: $id (expected integer)")
            return null
        }
        
        val tripResponse = postgresRepository.getTrip(tripId)
        return tripResponse?.toTrip()
    }
    
    /**
     * Convert TripResponse (database DTO) to Trip (database model)
     */
    private fun TripResponse.toTrip(): Trip {
        return Trip(
            tripTitle = this.tripTitle,
            tripDescription = this.tripDescription,
            tripLocation = this.tripLocation,
            tripDuration = this.tripDuration,
            userId = this.userId
        )
    }
}

