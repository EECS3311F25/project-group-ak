package org.example.project.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.encodeToString
import io.ktor.client.statement.*
import org.example.project.config.AIConfig
import org.example.project.trip.Trip
import org.slf4j.LoggerFactory


class AISummaryService(private val config : AIConfig){
    private val logger = LoggerFactory.getLogger(AISummaryService::class.java)


    private val httpClient = HttpClient(CIO){
        install(ContentNegotiation){
            json(
            Json{
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /*
    Suspend function can pause it's execution without blocking thread and then resume later
    */

    suspend fun generateSummary(trip: Trip, tripId: String? = null) : String{
        if (!config.isValid()) {
            throw AIServiceException("Python AI service URL is not configured. Set AI_API_URL environment variable")
        }
    
        try {
            val tripIdentifier = tripId ?: trip.tripTitle ?: "unknown"
            logger.info("Generating summary for trip : $tripIdentifier")

            val response = callAIService(trip)
            logger.info("Successfully generated summary for trip: $tripIdentifier")
            return response

        }

        catch (e : Exception){
            val tripIdentifier = tripId ?: trip.tripTitle ?: "unknown"
            logger.error("Failed to generate summary for trip $tripIdentifier: ${e.message}",e)
            throw AIServiceException("Failed to generate summary: ${e.message}",e )
        }
    
    } 


    /*
    Calls Python AI service with trip data
    Python service handles prompt formatting and AI API call (Claude/OpenAI)
    */
    private suspend fun callAIService(trip: Trip): String {
        // Serialize Trip to JSON
        val tripJson = Json.encodeToString(Trip.serializer(), trip)
        val tripJsonElement = Json.parseToJsonElement(tripJson)
        
        // Request body for Python service - send trip data instead of formatted prompt
        val requestBody = buildJsonObject {
            put("trip", tripJsonElement)
            put("model", config.model)
            put("max_tokens", config.maxTokens)
            put("temperature", config.temperature)
        }

        try {
            // Call Python service
            val httpResponse: HttpResponse = httpClient.post(config.apiUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Parse JSON response manually
            val responseText = httpResponse.bodyAsText()
            val jsonElement = Json.parseToJsonElement(responseText)
            val jsonObject = jsonElement.jsonObject

            // Extract summary from Python service response
            val summary = jsonObject["summary"]?.jsonPrimitive?.content
                ?: throw AIServiceException(
                    "Python service returned empty response: ${jsonObject["error"]?.jsonPrimitive?.content ?: "Unknown error"}"
                )

            return summary.trim()
        } catch (e: Exception) {
            logger.error("Error calling Python AI service: ${e.message}", e)
            throw AIServiceException("Failed to call Python AI service: ${e.message}", e)
        }
    }

    fun close() {
        httpClient.close()
    }
}

class AIServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)
