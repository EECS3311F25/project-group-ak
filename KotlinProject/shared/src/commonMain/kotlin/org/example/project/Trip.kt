package org.example.project

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

//  every serializable class must be marked @Serializable

@Serializable
data class Trip (
    @SerialName("trip_id")
    val tripID: Int,
    @SerialName("trip_title")
    val tripTitle: String,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("trip_location")
    val tripLocation: String,

    //  TODO: implement type for date + time
    @SerialName("trip_start_date")
    val tripStartDate: String,
    @SerialName("trip_end_date")
    val tripEndDate: String) {

    //  TODO: implement foreign key UserID (the User that created this trip)
}
