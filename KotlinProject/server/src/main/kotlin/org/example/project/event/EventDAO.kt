package org.example.project.event

import Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.project.trip.Location
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.example.project.trip.TripTable


/**
References:
-   https://ktor.io/docs/server-integrate-database.html#create-mapping
-   https://www.jetbrains.com/help/exposed/working-with-tables.html#nullable

TL;DR: the DAO API provides abstractions for defining database tables, and performing CRUD ops on them
 */


/**
Table / IntIdTable(db_table_name)
-   IntIdTable corresponds to a table with an auto column for entry id (i.e surrogate key)
 */

//  TODO: improve logic of field nullability

/**
 * Database table for Events.
 * event_location stores a JSON-encoded Location object.
 */
object EventTable : IntIdTable("events") {
    val eventTitle = varchar("event_title", 100)
    val eventDescription = varchar("event_description", 500)
    val eventDuration = varchar("event_duration", 200)
    val eventLocation = varchar("event_location", 500)   // <-- Location stored as JSON
    val tripId = reference("trip_id", TripTable, onDelete = ReferenceOption.CASCADE)
}

/**
 * DAO (Data Access Object) that maps an Event row.
 * Converts location JSON <-> Location data class.
 */
class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventTable)

    var eventTitle: String by EventTable.eventTitle
    var eventDescription: String by EventTable.eventDescription

    // Duration JSON encoding/decoding
    var stringDuration by EventTable.eventDuration
    var eventDuration: Duration
        get() = Json.decodeFromString(stringDuration)
        set(value) { stringDuration = Json.encodeToString(value) }

    // Location JSON encoding/decoding (correct Toni design)
    var stringLocation by EventTable.eventLocation
    var location: Location
        get() = Json.decodeFromString(stringLocation)
        set(value) { stringLocation = Json.encodeToString(value) }

    var trip by TripTable referencedOn EventTable.tripId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) { suspendTransaction(statement = block) }

/**
 * Convert DAO → Response DTO for frontend.
 */
fun EventDAO.toResponseDto() = EventResponse(
    id.value,
    eventTitle,
    eventDescription,
    eventDuration,
    location,        // <-- already decoded from JSON
    trip.id.value
)