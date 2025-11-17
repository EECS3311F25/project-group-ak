package org.example.project.trip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Trip (
    //  Trip ID is an automatically generated column (surrogate key) via IntIdTable
    //  -   reference: https://www.jetbrains.com/help/exposed/working-with-tables.html#dao-table-def
    @SerialName("trip_title")
    val tripTitle: String?,
    @SerialName("trip_description")
    val tripDescription: String?,

    //  TODO: implement Location data type (implement w/ map view)
    @SerialName("trip_location")
    val tripLocation: String?,

    //  TODO: implement type for date + time
    @SerialName("trip_duration")
    val tripDuration: String,

    //  Foreign key - User.id
    @SerialName("user_id")
    val userid: Int
)