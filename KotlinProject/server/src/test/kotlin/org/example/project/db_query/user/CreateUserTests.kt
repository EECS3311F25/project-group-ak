package org.example.project.db_query.user

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.server.testing.testApplication

import org.example.project.module
import org.example.project.user.User
import org.example.project.user.UserRetrieveResponse


class CreateUserTests {

    /**
     *  Valid user registration
     *  @result Account will be created without any errors and exceptions + returns Created status code
     */
    @Test
    fun userCreate_TestSuccess_1() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user1", "user1@gmail.com", "password1"))
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val responseBody = response.body<UserRetrieveResponse>()
        assertEquals("User registered successfully", responseBody.message)

        val responseData = responseBody.data
        assertEquals("user1", responseData.userName)
        assertEquals("user1@gmail.com", responseData.userEmail)
    }

    /**
     *  Valid user registration
     *  @result Account will be created without any errors and exceptions + returns Created status code
     */
    @Test
    fun userCreate_TestSuccess_2() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user2", "user2@gmail.com", "password2"))
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val responseBody = response.body<UserRetrieveResponse>()
        assertEquals("User registered successfully", responseBody.message)

        val responseData = responseBody.data
        assertEquals("user2", responseData.userName)
        assertEquals("user2@gmail.com", responseData.userEmail)
    }

    /**
     *  Empty name (-> invalid), but valid email and password
     *  @result Account will not be created + returns BadRequest status code
     */
    @Test
    fun userCreate_TestNameEmpty() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("", "user3@gmail.com", "password3"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("User registration failed", response.bodyAsText())
    }

    /**
     *  Empty email (-> invalid), but valid name and password
     *  @result Account will not be created + returns BadRequest status code
     */
    @Test
    fun userCreate_TestEmailEmpty() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user4", "", "password4"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("User registration failed", response.bodyAsText())
    }

    /**
     *  Email without "@" (-> invalid), but valid name and password
     *  @result Account will not be created + returns BadRequest status code
     */
    @Test
    fun userCreate_TestEmailWithoutAt() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user5", "user5gmail.com", "password5"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("User registration failed", response.bodyAsText())
    }

    /**
     *  Empty password (-> invalid), but valid name and email
     *  @result Account will not be created + returns BadRequest status code
     */
    @Test
    fun userCreate_TestPasswordEmpty() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user6", "user6@gmail.com", ""))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("User registration failed", response.bodyAsText())
    }

    /**
     *  Password too weak (-> invalid), but valid name and password
     *  @result Account will not be created + returns BadRequest status code
     */
    @Test
    fun userCreate_TestPasswordTooWeak() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response: HttpResponse = client.post("/user/register") {
            contentType(ContentType.Application.Json)
            setBody(User("user7", "user7@gmail.com", "weakpsw"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("User registration failed", response.bodyAsText())
    }
}