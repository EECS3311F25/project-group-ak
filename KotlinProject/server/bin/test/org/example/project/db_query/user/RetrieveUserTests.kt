package org.example.project.db_query.user

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import org.example.project.module
import org.example.project.user.UserRetrieveResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class RetrieveUserTests {

    /**
     *  User exists (-> valid)
     *  @result Get account respond without any errors and exceptions + returns OK status code
     */
    @Test
    fun userRetrieve_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.get("/user/1") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<UserRetrieveResponse>()

        assertEquals("User retrieved successfully", responseBody.message)

        val responseData = responseBody.data
        assertEquals("user1", responseData.userName)
        assertEquals("user1@gmail.com", responseData.userEmail)
    }

    /**
     *  User exists (-> valid)
     *  @result Get account respond without any errors and exceptions + returns OK status code
     */
    @Test
    fun userRetrieve_TestSuccess_2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.get("/user/2") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<UserRetrieveResponse>()

        assertEquals("User retrieved successfully", responseBody.message)

        val responseData = responseBody.data
        assertEquals("user2", responseData.userName)
        assertEquals("user2@gmail.com", responseData.userEmail)
    }

    /**
     *  User not found (-> invalid)
     *  @result User account not retrieved + returns Not Found status code
     */
    @Test
    fun userRetrieve_TestUserNotFound() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.get("/user/3") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("User not found", response.bodyAsText())
    }
}