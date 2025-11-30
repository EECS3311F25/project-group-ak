package org.example.project.db_query.trip

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


class RetrieveTripTests {

    /**
     *  User exists (-> valid)
     *  @result Get account without any errors and exceptions + returns OK status code
     */
    @Test
    fun tripRetrieve_TestSuccess_1() = testApplication {
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
}