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
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.project.data.api.dto.TripSummaryResponse
import org.example.project.data.api.dto.ErrorResponse


class TripApiService(
    private val client : HttpClient,
    private val baseUrl : String = "http://localhost:8080"
) {
    // suspend function can pause the function without breaking the threads
    suspend fun generateTripSummary(tripId:String) : Result<TripSummaryResponse>{
        return try {
            val httpResponse: HttpResponse = client.post("$baseUrl/api/trips/$tripId/summary"){
                contentType(ContentType.Application.Json)
            }
            
            if (httpResponse.status == HttpStatusCode.OK) {
                val response = httpResponse.body<TripSummaryResponse>()
                Result.success(response)
            } else {
                // Try to parse error response
                val errorResponse = httpResponse.body<ErrorResponse>()
                Result.failure(Exception("${errorResponse.error}: ${errorResponse.message}"))
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}