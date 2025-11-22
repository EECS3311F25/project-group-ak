package org.example.project.repository

import org.example.project.trip.Trip
import org.example.project.trip.TripRepositoryMock
import org.slf4j.LoggerFactory

/**
 * PostgreSQL implementation of TripRepository.
 * 
 * Currently uses TripRepositoryMock as a fallback until full database integration is complete.
 * TODO: Replace with actual PostgreSQL database queries using Exposed ORM.
 */
class TripRepositoryImpl : TripRepository {
    private val logger = LoggerFactory.getLogger(TripRepositoryImpl::class.java)

    override suspend fun getById(id: String): Trip? {
        // TODO: Replace with actual database query
        // Example future implementation:
        // return suspendTransaction {
        //     TripTable.select { TripTable.id eq id.toInt() }
        //         .firstOrNull()
        //         ?.let { daoToTripModel(it) }
        // }
        
        // For now, use mock repository
        logger.debug("Getting trip by ID: $id (using mock repository)")
        return TripRepositoryMock.getById(id)
    }
}

