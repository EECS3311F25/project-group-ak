package org.example.project.trip

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

//  TODO: revise TripTable, in correspondence to the database table's entity relations
//  TODO: improve logic of field nullability

object TripTable : IntIdTable("trip") {
    val tripTitle = varchar("trip_title", 100)
    val tripDescription = varchar("trip_description", 150)
    val tripLocation = varchar("trip_location", 255)
    val tripStartDate = varchar("trip_start_date", 50) // TODO: implement date type
    val tripEndDate = varchar("trip_end_date", 50) // TODO: implement date type

    // TODO: Add foreign key associated with User table (createdBy)
    // val createdByUserId = reference("created_by_user_id", UserTable)
}


/**
    Entity object maps Trip type's fields to columns in the database's Trip table
 */
class TripDAO(tripId: EntityID<Int>) : IntEntity(tripId) {
    companion object : IntEntityClass<TripDAO>(TripTable)

    var tripTitle by TripTable.tripTitle
    var tripDescription by TripTable.tripDescription
    var tripLocation by TripTable.tripLocation
    var tripStartDate by TripTable.tripStartDate
    var tripEndDate by TripTable.tripEndDate

    //  TODO: add foreign key to User table
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun daoToTripModel(dao: TripDAO) = Trip(
    dao.tripTitle,
    dao.tripDescription,
    dao.tripLocation,
    dao.tripStartDate,
    dao.tripEndDate
)