package org.example.project.db.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.example.project.db.tables.TripTable

/**
 * DAO class for Trip table operations.
 * Maps TripTable rows to Kotlin objects using Exposed's DAO pattern.
 */
class TripDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TripDAO>(TripTable)

    var tripTitle by TripTable.tripTitle
    var tripLocation by TripTable.tripLocation
    var tripStartDate by TripTable.tripStartDate
    var tripEndDate by TripTable.tripEndDate
}

/**
 * Converts TripDAO to shared Trip model.
 */
fun daoToTripModel(dao: TripDAO) = org.example.project.Trip(
    tripID = dao.id.value,
    tripTitle = dao.tripTitle,
    tripLocation = dao.tripLocation,
    tripStartDate = dao.tripStartDate,
    tripEndDate = dao.tripEndDate
)

