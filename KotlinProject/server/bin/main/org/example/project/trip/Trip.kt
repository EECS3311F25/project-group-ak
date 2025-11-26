package org.example.project.trip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import Duration


@Serializable
data class Trip (
    //  Trip ID is an automatically generated column (surrogate key)
    //  -   reference: https://www.jetbrains.com/help/exposed/working-with-tables.html#dao-table-def
    @SerialName("trip_title")
    val tripTitle: String?,
    @SerialName("trip_description")
    val tripDescription: String?,

    //  TODO: implement Location data type
    //  TODO: implement Location type's logic + interaction w/ app's map view
    @SerialName("trip_location")
    val tripLocation: String?,
    @SerialName("trip_duration")
    val tripDuration: Duration,

    //  Foreign key to User table
    @SerialName("user_id")
    val userId: Int?,
)