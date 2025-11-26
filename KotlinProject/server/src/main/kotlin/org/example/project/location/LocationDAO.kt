package org.example.project.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.event.EventDAO
import org.example.project.event.EventTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

/**
 * Database table for locations with GPS coordinates
 */
object LocationTable : IntIdTable("locations") {
    val latitude = double("latitude")
    val longitude = double("longitude")
    val address = varchar("address", 255).nullable()
    val title = varchar("title", 255).nullable()
    
    // Foreign key to Event table
    val eventId = reference("event_id", EventTable, onDelete = ReferenceOption.CASCADE)
}

/**
 * Entity object maps Location fields to columns in the database's locations table
 */
class LocationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LocationDAO>(LocationTable)

    var latitude by LocationTable.latitude
    var longitude by LocationTable.longitude
    var address by LocationTable.address
    var title by LocationTable.title
    var eventId by EventDAO referencedOn LocationTable.eventId
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }

fun LocationDAO.toResponseDto() = LocationResponse(
    id.value,
    latitude,
    longitude,
    address,
    title,
    eventId.id.value
)
