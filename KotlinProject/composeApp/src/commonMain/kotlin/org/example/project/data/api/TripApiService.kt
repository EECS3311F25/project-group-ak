package org.example.project.data.api

/*
imports ktor client : provides access to >> HttpClient, clientConfiguration, API calls

call : turns the HTTP response into your kotlin data class

request : access to request-building functions such as get() post() 

Http : imports HTTP related classes (get, post, put etc)
*/


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.example.project.data.api.dto.TripSummaryResponse


class TripApiService(
    private val client : HttpClient,
    private val baseUrl : String = "http://localhost:8080"
)

{   // suspend function can pause the function without breaking the threads
    suspend fun generateTripSummary(tripId:String) : Result<TripSummaryResponse>{
        return try {
            val response = client.post("$baseUrl/api/trips/$tripId/summary"){
                contentType(ContentType.Application.Json)
            }.body<TripSummaryResponse>()
            Result.success(response)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}