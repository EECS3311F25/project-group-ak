package org.example.project.trip

import kotlinx.serialization.Serializable


@Serializable
data class Trip (
    //  Trip ID is an automatically generated column (surrogate key)
    //  -   reference: https://www.jetbrains.com/help/exposed/working-with-tables.html#dao-table-def
    //  @SerialName("trip_title")
    val tripTitle: String?,
    //  @SerialName("trip_description")
    val tripDescription: String?,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    //  @SerialName("trip_location")
    val tripLocation: String?,

    //  TODO: implement type for date + time
    //  @SerialName("trip_start_date")
    val tripStartDate: String?,
    //  @SerialName("trip_end_date")
    val tripEndDate: String?,

    //  TODO: implement foreign key UserID (the User that created this trip)
)