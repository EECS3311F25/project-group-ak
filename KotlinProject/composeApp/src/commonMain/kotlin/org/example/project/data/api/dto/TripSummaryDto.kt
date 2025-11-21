package org.example.project.data.api.dto

import kotlinx.serialization.Serializable
/*
what does Serializable do
 - it let's you convert kotlin to json and json to kotlin object

 -- In data class the methods like equals() hashcode can overided automatically according to use to the use

*/

@Serializable
data class TripSummaryResponse(
    val summary : String,
    val generatedAt: String
)

@Serializable
data class ErrorResponse(
    val error : String,
    val message : String
)




