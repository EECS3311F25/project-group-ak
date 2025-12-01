package org.example.project.trip

import Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

import org.example.project.user.UserDAO
import org.example.project.user.UserTable
import kotlin.Int


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

object TripTable : IntIdTable("trips") {
    val tripTitle = varchar("trip_title", 100)
    val tripDescription = varchar("trip_description", 150)
    val tripLocation = varchar("trip_location", 255)
    val tripDuration = varchar("trip_duration", 200)

    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
}


/**
    Entity object maps Trip type's fields to columns in the database's Trip table
 */
class TripDAO(tripId: EntityID<Int>) : IntEntity(tripId) {
    companion object : IntEntityClass<TripDAO>(TripTable)

    var tripTitle: String by TripTable.tripTitle
    var tripDescription: String by TripTable.tripDescription
    var tripLocation: String by TripTable.tripLocation
    var stringTripDuration by TripTable.tripDuration
    var tripDuration: Duration
        get() = Json.decodeFromString<Duration>(stringTripDuration)
        set(value) { stringTripDuration = Json.encodeToString(value) }

    var userId by UserDAO referencedOn TripTable.userId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

//  TODO: implement Location data type
//  TODO: implement Location type's logic + interaction w/ app's map view
fun TripDAO.toResponseDto() = TripResponse(
    id.value,
    tripTitle,
    tripDescription,
    tripLocation,
    tripDuration,
    userId.id.value
)

fun daoToTripModel(dao: TripDAO) = Trip(
    dao.tripTitle,
    dao.tripDescription,
    dao.tripLocation,
    dao.tripDuration,
    dao.userId.id.value
)