package org.example.project.db.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.example.project.db.tables.EventTable

/**
 * DAO class for Event table operations.
 * Maps EventTable rows to Kotlin objects using Exposed's DAO pattern.
 */
class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventTable)

    var eventTitle by EventTable.eventTitle
    var eventDescription by EventTable.eventDescription
    var eventLocation by EventTable.eventLocation
    var tripStartDate by EventTable.tripStartDate
    var tripEndDate by EventTable.tripEndDate
    var tripId by EventTable.tripId
}

/**
 * Converts EventDAO to shared Event model.
 */
fun daoToEventModel(dao: EventDAO) = org.example.project.Event(
    eventID = dao.id.value,
    eventTitle = dao.eventTitle,
    eventDescription = dao.eventDescription,
    eventLocation = dao.eventLocation,
    tripStartDate = dao.tripStartDate,
    tripEndDate = dao.tripEndDate
)

