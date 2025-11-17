package org.example.project.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.trip.TripDAO
import org.example.project.trip.TripTable
import org.example.project.user.UserDAO
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

//  TODO: revise UserTable, in correspondence to the database table's entity relations
//  TODO: improve logic of field nullability

object EventTable : IntIdTable("event") {
    val eventTitle = varchar("event_title", 100)
    val eventDescription = varchar("event_description", 500)
    val eventLocation = varchar("event_location", 255)
    val eventDuration = varchar("event_duration", 50)

    val tripId = reference("trip_id", TripTable)
}

/**
Entity object maps Trip type's fields to columns in the database's Trip table
 */
class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventTable)

    var eventTitle by EventTable.eventTitle
    var eventDescription by EventTable.eventDescription
    var eventLocation by EventTable.eventLocation
    var eventDuration by EventTable.eventDuration

    var tripId by TripDAO referencedOn EventTable.tripId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun daoToEventModel(dao: EventDAO) = Event(
    eventTitle = dao.eventTitle,
    eventDescription = dao.eventDescription,
    eventLocation = dao.eventLocation,
    dao.eventDuration,
    dao.tripId.id.value
)
