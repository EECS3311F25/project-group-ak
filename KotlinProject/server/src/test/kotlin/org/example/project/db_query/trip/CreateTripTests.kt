package org.example.project.db_query.trip

import Duration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalTime
import org.example.project.module
import org.example.project.trip.TripCreateRequest
import kotlin.test.Test
import kotlin.test.assertEquals

import org.example.project.trip.TripRetrieveResponse
import org.example.project.user.UserCreateRequest
import org.example.project.user.UserRetrieveResponse

class CreateTripTests {
    /**
     *  Valid trip creation
     *  @result Trip associated with account will be created without any errors and exceptions + returns Created status code
     */
    @Test
    fun tripCreate_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        /**
         * Create new User
         */
        val userResponse: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateRequest("user0", "user0@gmail.com", "password0"))
        }
        println("\n" + userResponse.bodyAsText() + "\n")
        val userResponseData = userResponse.body<UserRetrieveResponse>().data
        val userId = userResponseData.id

        /**
         * Create new Trip under the new User
         */
        val response: HttpResponse = client.post("/user/$userId/trip") {
            contentType(ContentType.Application.Json)
            setBody(TripCreateRequest(
                    "Trip 1",
                    "Trip Description 1",
                    "Trip Location 1",
                    Duration(
                        startDate = LocalDate(2026, Month.FEBRUARY, 21),
                        startTime = LocalTime(hour = 8, minute = 30, second = 15),
                        endDate = LocalDate(2026, Month.FEBRUARY, 28),
                        endTime = LocalTime(hour = 8, minute = 30, second = 15)
                    ),
                    userId
                )
            )
        }
        println("\n" + response.bodyAsText() + "\n")
        assertEquals(HttpStatusCode.Created, response.status)

        val responseBody = response.body<TripRetrieveResponse>()
        assertEquals("Trip created successfully", responseBody.message)

        val responseData = responseBody.data
        assertEquals("Trip 1", responseData.tripTitle)
        assertEquals("Trip Description 1", responseData.tripDescription)
        assertEquals("Trip Location 1", responseData.tripLocation)
        assertEquals(Duration(
            startDate = LocalDate(2026, Month.FEBRUARY, 21),
            startTime = LocalTime(hour = 8, minute = 30, second = 15),
            endDate = LocalDate(2026, Month.FEBRUARY, 28),
            endTime = LocalTime(hour = 8, minute = 30, second = 15)),
        responseData.tripDuration)
    }
}