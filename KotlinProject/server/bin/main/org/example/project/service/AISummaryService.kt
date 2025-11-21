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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    suspend fun generateSummary(trip: Trip) : String{
        if (!config.isValid()) {
            throw AIServiceException("Python AI service URL is not configured. Set AI_API_URL environment variable")
        }
    
        try {
            val prompt = formatPrompt(trip)
            logger.info("Generating summary for trip : ${trip.id}")

            val response = callAIService(prompt)
            logger.info("Successfully generated summary for trip: ${trip.id}")
            return response

        }

        catch (e : Exception){
            logger.error("Failed to generate summary for trip ${trip.id}: ${e.message}",e)
            throw AIServiceException("Failed to generate summary: ${e.message}",e )
        }
    
    } 



    // format prompt
    private fun formatPrompt(trip: Trip) : String {
        val participants = trip.users.joinToString(", ")
        val dateRange = "${trip.duration.start} to ${trip.duration.end}"

        // if event is empty
        val eventsText = if (trip.events.isEmpty()){
            "No specific events planned yet "
        }  
        else {
            trip.events.mapIndexed { index, event ->
                val day = index + 1
                val timeRange = "${event.duration.start} - ${event.duration.end}"
                
                val locationText = if (event.location != null) {
                    " at (${event.location.latitude}, ${event.location.longitude})"
                } else {
                    ""
                }

                "- Day $day: ${event.title} ($timeRange)$locationText${if (event.description != null) " - ${event.description}" else ""}"
            }.joinToString("\n")
        }


        return """
            Generate a brief, engaging summary (2-3 sentences) for this trip:
            
            Title: ${trip.name}
            Dates: $dateRange
            Owner: ${trip.owner}
            Participants: $participants
            
            Events:
            $eventsText
            
            Make it sound exciting and capture the essence of the trip. Keep it concise and appealing.
        """.trimIndent()

    }


    /*
    Calls Python AI service instead of AI API directly
    Python service handles the actual AI API call (Claude/OpenAI)
    */
    private suspend fun callAIService(prompt: String): String {
        // Request body for Python service using JsonObject for mixed types
        val requestBody = buildJsonObject {
            put("prompt", prompt)
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
