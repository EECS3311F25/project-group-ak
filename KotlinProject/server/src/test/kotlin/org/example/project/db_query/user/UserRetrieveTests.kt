package org.example.project.db_query.user

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
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRetrieveTests {

    /**
     *  User exists (-> valid)
     *  @result Get account without any errors and exceptions + returns OK status code
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

        val response: HttpResponse = client.get("/user/user1") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User retrieved successfully", response.bodyAsText())
    }

    /**
     *  User exists (-> valid)
     *  @result Get account without any errors and exceptions + returns OK status code
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

        val response: HttpResponse = client.get("/user/user2") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User retrieved successfully", response.bodyAsText())
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

        val response: HttpResponse = client.get("/user/user3") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("User not found", response.bodyAsText())
    }
}