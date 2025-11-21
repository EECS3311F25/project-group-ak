package org.example.project.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//  TODO: write HTTPS routing in this module
//  docs: https://ktor.io/docs/server-integrate-database.html#switch-repo
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Serialization.kt

/**
 * User HTTP routes.
 *
 * Flow:
 *  - Application.module() creates PostgresUserRepository
 *  - Application.configureUserSerialization(userRepository)
 *  - These handlers use repository only (validation is in UserService).

 * Endpoints:
 *  GET    /user/{id}         -> get user by user ID
 *  POST   /user/register           -> create new user
 *  PUT    /user/{id}/password         -> update user password
 *  DELETE /user/{id}/delete  -> delete user by username
 */
fun Application.configureUserSerialization(userRepository: PostgresUserRepository) {

    routing {

        route("/user") {

            //  GET "/user/{id}"  ->  get user by id
            get("/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                val user = userRepository.getUserById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@get
                }

                call.respond(HttpStatusCode.OK, "User retrieved successfully")
            }

            //  POST "/user/register"   ->  create new user
            post("/register") {
                try {
                    val user = call.receive<User>()

                    userRepository.addUser(user)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.Created,
                                "User successfully registered"
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "User registration failed"
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Internal server error, registration failed"
                    )
                }
            }

            //  PUT "/user/password" - update user password
            put("/{id}/password") {
                try {
                    val user = call.receive<User>()
                    val userId = call.parameters["id"]?.toIntOrNull()

                    userRepository.updateUserPassword(userId, user.userPassword)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.OK,
                                "Password updated successfully"
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                "Password update failed"
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Password update failed: ${e.message}"
                    )
                }
            }

            //  DELETE "/user/{id}/delete" - delete user by username
            delete("/{id}/delete") {
                val userId = call.parameters["id"]?.toIntOrNull()
                userRepository.deleteUserByUserId(userId)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.NoContent,
                            "User deleted successfully"
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound,
                            "User does not exist"
                        )
                    }
            }
        }
    }
}