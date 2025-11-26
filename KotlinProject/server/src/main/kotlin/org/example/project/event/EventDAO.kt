package org.example.project.event

import Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.project.trip.TripDAO
import org.example.project.trip.TripTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction


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

object EventTable : IntIdTable("events") {
    val eventTitle = varchar("event_title", 100)
    val eventDescription = varchar("event_description", 500)
    val eventLocation = varchar("event_location", 255)
    val eventDuration = varchar("event_duration", 200)

    //  Foreign key to Trip table
    val tripId = reference("trip_id", TripTable, onDelete = ReferenceOption.CASCADE)
}

/**
Entity object maps Event type's fields to columns in the database's Event table
 */
class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventTable)

    var eventTitle by EventTable.eventTitle
    var eventDescription by EventTable.eventDescription
    var eventLocation by EventTable.eventLocation
    var stringEventDuration by EventTable.eventDuration
    var eventDuration: Duration
        get() = Json.decodeFromString(stringEventDuration)
        set(value) { stringEventDuration = Json.encodeToString(value) }

    var tripId by TripDAO referencedOn EventTable.tripId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun EventDAO.toResponseDto() = EventResponse(
    id.value,
    eventTitle,
    eventDescription,
    eventLocation,
    eventDuration,
    tripId.id.value
)

fun daoToEventModel(dao: EventDAO) = Event(
    eventTitle = dao.eventTitle,
    eventDescription = dao.eventDescription,
    eventLocation = dao.eventLocation,
    dao.eventDuration,
    dao.tripId.id.value
)