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
 *  - Handlers use repository only (validation is in UserService).
 *
 * Endpoints:
 *  GET    /user/{userName}         -> get user by username
 *  POST   /user/register           -> create new user
 *  PUT    /user/password           -> update user password
 *  DELETE /user/{userName}/delete  -> delete user by username
 */
 
fun Application.configureUserSerialization(userRepository: PostgresUserRepository) {

    routing {
        
        route("/user") {

            // GET /user/{userName}  -> get user by username
            get("/{userName}") {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@get
                }

                val user = userRepository.getUserByName(userName)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                } else {
                    // For now we just return a simple message (frontend can change later)
                    call.respond(HttpStatusCode.OK, "User retrieved successfully")
                }
            }

            // POST /user/register  -> create new user
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
                        .onFailure {
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

            // PUT /user/password  -> update user password
            put("/password") {
                try {
                    val user = call.receive<User>()

                    userRepository.updateUserPassword(user.userName, user.userPassword)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.OK,
                                "Password updated successfully"
                            )
                        }
                        .onFailure {
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

            // DELETE /user/{userName}/delete  -> delete user by username
            delete("/{userName}/delete") {
                val userName = call.parameters["userName"]
                if (userName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing userName")
                    return@delete
                }

                userRepository.deleteUserByUsername(userName)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.NoContent,
                            "User deleted successfully"
                        )
                    }
                    .onFailure {
                        call.respond(
                            HttpStatusCode.NotFound,
                            "User does not exist"
                        )
                    }
            }
        }
    }
}