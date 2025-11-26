package org.example.project.db_query.trip

import Duration
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.example.project.module
import org.example.project.trip.Trip
import org.example.project.trip.TripCreateRequest
import org.example.project.trip.TripRetrieveResponse
import org.example.project.user.User
import org.example.project.user.UserCreateRequest
import org.example.project.user.UserRetrieveResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class TripRouteTests {

    @Test
    fun tripCRUD_Success2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val tripDeleteResponse: HttpResponse = client.delete("/user/6/trip/5") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, tripDeleteResponse.status)
        assertEquals("Trip deleted successfully", tripDeleteResponse.bodyAsText())

        /**
         *  Run valid user delete
         */
        val userDeleteResponse: HttpResponse = client.delete("/user/6/delete") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, userDeleteResponse.status)
        assertEquals("User deleted successfully", userDeleteResponse.bodyAsText())
    }


    /**
     *  Valid user creation -> valid trip creation (associated w/ created user) -> valid trip update -> valid trip retrieval -> valid trip delete -> valid user delete
     *  @result Trip associated with account will be created without any errors and exceptions + returns Created status code
     */
    @Test
    fun tripCRUD_Success1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        /**
         *  Valid user creation
         */
        val userCreateResponse: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateRequest("user1", "user1@gmail.com", "password1"))
        }
        println("\n" + userCreateResponse.bodyAsText() + "\n")

        val userResponseData = userCreateResponse.body<UserRetrieveResponse>().data
        val userId = userResponseData.id

        /**
         *  Run valid trip creation (associated with the user above)
         */
        val tripCreateResponse: HttpResponse = client.post("/user/$userId/trip") {
            contentType(ContentType.Application.Json)
            setBody(TripCreateRequest(
                "Trip 1",
                "Trip Description 1",
                "Trip Location 1",
                Duration(
                    startDate = LocalDate(2026, Month.FEBRUARY, 21),
                    startTime = LocalTime(hour = 8, minute = 30, second = 15),
                    endDate = LocalDate(2026, Month.FEBRUARY, 28),
                    endTime = LocalTime(hour = 8, minute = 30, second = 15)),
                userId
            ))
        }
        println("\n" + tripCreateResponse.bodyAsText() + "\n")

        val tripCreateResBody = tripCreateResponse.body<TripRetrieveResponse>()
        assertEquals("Trip created successfully", tripCreateResBody.message)

        val tripCreateResData = tripCreateResBody.data
        assertEquals("Trip 1", tripCreateResData.tripTitle)
        assertEquals("Trip Description 1", tripCreateResData.tripDescription)
        assertEquals("Trip Location 1", tripCreateResData.tripLocation)
        assertEquals(Duration(
            startDate = LocalDate(2026, Month.FEBRUARY, 21),
            startTime = LocalTime(hour = 8, minute = 30, second = 15),
            endDate = LocalDate(2026, Month.FEBRUARY, 28),
            endTime = LocalTime(hour = 8, minute = 30, second = 15)),
            tripCreateResData.tripDuration)

        val tripId = tripCreateResData.id

        /**
         *  Run valid trip update
         */
        val tripUpdateResponse: HttpResponse = client.put("/user/$userId/trip/$tripId") {
            contentType(ContentType.Application.Json)
            setBody(
                Trip(
                    "Updated Trip 1",
                    "Updated Trip Description 1",
                    "Updated Trip Location 1",
                    Duration(
                        startDate = LocalDate(2026, Month.MARCH, 21),
                        startTime = LocalTime(hour = 8, minute = 30, second = 16),
                        endDate = LocalDate(2026, Month.MARCH, 28),
                        endTime = LocalTime(hour = 8, minute = 30, second = 16)
                    ),
                    userId
                )
            )
        }
        assertEquals(HttpStatusCode.OK, tripUpdateResponse.status)
        assertEquals("Trip updated successfully", tripUpdateResponse.bodyAsText())

        /**
         * Run valid trip retrieval
         */
        val tripGetResponse: HttpResponse = client.get("/user/$userId/trip/$tripId") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, tripGetResponse.status)

        val getResponseBody = tripGetResponse.body<TripRetrieveResponse>()
        assertEquals("Trip retrieved successfully", getResponseBody.message)

        val getResponseData = getResponseBody.data
        assertEquals("Updated Trip 1", getResponseData.tripTitle)
        assertEquals("Updated Trip Description 1", getResponseData.tripDescription)
        assertEquals("Updated Trip Location 1", getResponseData.tripLocation)
        assertEquals(Duration(
            startDate = LocalDate(2026, Month.MARCH, 21),
            startTime = LocalTime(hour = 8, minute = 30, second = 16),
            endDate = LocalDate(2026, Month.MARCH, 28),
            endTime = LocalTime(hour = 8, minute = 30, second = 16)),
            getResponseData.tripDuration)

        /**
         *  Run valid trip delete
         */
        val tripDeleteResponse: HttpResponse = client.delete("/user/$userId/trip/$tripId") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, tripDeleteResponse.status)
        assertEquals("Trip deleted successfully", tripDeleteResponse.bodyAsText())

        /**
         *  Run valid user delete
         */
        val userDeleteResponse: HttpResponse = client.delete("/user/$userId/delete") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, userDeleteResponse.status)
        assertEquals("User deleted successfully", userDeleteResponse.bodyAsText())
    }


}