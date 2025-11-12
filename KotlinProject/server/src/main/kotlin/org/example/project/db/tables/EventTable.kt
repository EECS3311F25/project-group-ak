package org.example.project.db.tables

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Exposed table definition for events.
 * Maps to the 'event' table in PostgreSQL.
 */
object EventTable : IntIdTable("event") {
    val eventTitle = varchar("event_title", 100)
    val eventDescription = varchar("event_description", 500)
    val eventLocation = varchar("event_location", 255)
    val tripStartDate = varchar("trip_start_date", 50) // TODO: Change to proper date type
    val tripEndDate = varchar("trip_end_date", 50) // TODO: Change to proper date type
    
    // Foreign key to Trip table
    val tripId = reference("trip_id", TripTable)
}

