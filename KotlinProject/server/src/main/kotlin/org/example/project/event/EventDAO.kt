package org.example.project.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.trip.TripTable
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
    val eventStartDate = varchar("trip_start_date", 50) // TODO: Change to proper date type
    val eventEndDate = varchar("trip_end_date", 50) // TODO: Change to proper date type

    //  Foreign key to Trip table
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
    var eventStartDate by EventTable.eventStartDate   // TODO: implement date type
    var eventEndDate by EventTable.eventEndDate   // TODO: implement date type

    // TODO: Add foreign key associated with Trip table (createdBy)
    // val createdByTripId = reference("created_by_trip_id", TripTable)
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun daoToEventModel(dao: EventDAO) = Event(
    eventTitle = dao.eventTitle,
    eventDescription = dao.eventDescription,
    eventLocation = dao.eventLocation,
    eventStartDate = dao.eventStartDate,
    eventEndDate = dao.eventEndDate
)
