package org.example.project.db_query.user

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
import org.example.project.module
import org.example.project.user.User
import org.example.project.user.UserCreateRequest
import org.example.project.user.UserRetrieveResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRouteTests {

    /**
     *  Valid user registration -> valid user password update -> valid user retrieval -> valid user deletion
     *  @result Account will not be in the DB after the CRUD op sequence
     */
    @Test
    fun userCRUD_Success1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        /**
         * Run valid user account creation
         */
        val userCreateResponse: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateRequest("user1", "user1@gmail.com", "password1"))
        }
        assertEquals(HttpStatusCode.Created, userCreateResponse.status)

        println("\n" + userCreateResponse.bodyAsText() + "\n")

        val createResponseBody = userCreateResponse.body<UserRetrieveResponse>()
        assertEquals("User registered successfully", createResponseBody.message)

        val createResponseData = createResponseBody.data
        assertEquals("user1", createResponseData.userName)
        assertEquals("user1@gmail.com", createResponseData.userEmail)

        val userId = createResponseData.id

        /**
         * Run valid user password update
         */
        val userUpdateResponse: HttpResponse = client.put("/user/$userId") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "user1@gmail.com", "newPassword1"))
        }
        assertEquals(HttpStatusCode.OK, userUpdateResponse.status)
        assertEquals("Password updated successfully", userUpdateResponse.bodyAsText())

        println("\n" + userUpdateResponse.bodyAsText() + "\n")

        /**
         * Run valid user account retrieval
         */
        val userGetResponse: HttpResponse = client.get("/user/$userId") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, userGetResponse.status)

        val getResponseBody = userGetResponse.body<UserRetrieveResponse>()
        assertEquals("User retrieved successfully", getResponseBody.message)

        val responseData = getResponseBody.data
        assertEquals("user1", responseData.userName)
        assertEquals("user1@gmail.com", responseData.userEmail)

        /**
         * Run valid user account deletion
         */
        val userDeleteResponse: HttpResponse = client.delete("/user/$userId/delete") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, userDeleteResponse.status)
        assertEquals("User deleted successfully", userDeleteResponse.bodyAsText())
    }

    @Test
    fun userCRUD_Success2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        /**
         * Run valid user account creation
         */
        val userCreateResponse: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateRequest("user2", "user2@gmail.com", "password2"))
        }
        assertEquals(HttpStatusCode.Created, userCreateResponse.status)

        println("\n" + userCreateResponse.bodyAsText() + "\n")

        val createResponseBody = userCreateResponse.body<UserRetrieveResponse>()
        assertEquals("User registered successfully", createResponseBody.message)

        val createResponseData = createResponseBody.data
        assertEquals("user2", createResponseData.userName)
        assertEquals("user2@gmail.com", createResponseData.userEmail)

        val userId = createResponseData.id

        /**
         * Run valid user password update
         */
        val userUpdateResponse: HttpResponse = client.put("/user/$userId") {
            contentType(ContentType.Application.Json)
            setBody(User("user2", "user2@gmail.com", "newPassword2"))
        }
        assertEquals(HttpStatusCode.OK, userUpdateResponse.status)
        assertEquals("Password updated successfully", userUpdateResponse.bodyAsText())

        println("\n" + userUpdateResponse.bodyAsText() + "\n")

        /**
         * Run valid user account retrieval
         */
        val userGetResponse: HttpResponse = client.get("/user/$userId") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.OK, userGetResponse.status)

        val getResponseBody = userGetResponse.body<UserRetrieveResponse>()
        assertEquals("User retrieved successfully", getResponseBody.message)

        val responseData = getResponseBody.data
        assertEquals("user2", responseData.userName)
        assertEquals("user2@gmail.com", responseData.userEmail)

        /**
         * Run valid user account deletion
         */
        val userDeleteResponse: HttpResponse = client.delete("/user/$userId/delete") {
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.NoContent, userDeleteResponse.status)
        assertEquals("User deleted successfully", userDeleteResponse.bodyAsText())
    }
}