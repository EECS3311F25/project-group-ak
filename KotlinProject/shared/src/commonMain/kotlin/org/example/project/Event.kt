package org.example.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

//  every serializable class must be marked @Serializable

@Serializable
data class Event (
    @SerialName("event_id")
    val eventID: Int,
    @SerialName("event_title")
    val eventTitle: String,
    @SerialName("event_description")
    val eventDescription: String,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("event_location")
    val eventLocation: String,

    //  TODO: implement type for date + time
    @SerialName("trip_start_date")
    val tripStartDate: String,
    @SerialName("trip_end_date")
    val tripEndDate: String) {

    //  TODO: implement foreign key TripID (the Trip this Event belongs to)
}