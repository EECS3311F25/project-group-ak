package org.example.project.db.tables

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Exposed table definition for trips.
 * Maps to the 'trip' table in PostgreSQL.
 */
object TripTable : IntIdTable("trip") {
    val tripTitle = varchar("trip_title", 100)
    val tripLocation = varchar("trip_location", 255)
    val tripStartDate = varchar("trip_start_date", 50) // TODO: Change to proper date type
    val tripEndDate = varchar("trip_end_date", 50) // TODO: Change to proper date type
    
    // TODO: Add foreign key to User table (createdBy)
    // val createdByUserId = reference("created_by_user_id", UserTable)
}
