package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.User
import org.example.project.dto.ApiResponse
import org.example.project.dto.UpdatePasswordRequest
import org.example.project.dto.UserRegisterRequest
import org.example.project.dto.UserResponse
import org.example.project.service.UserService

/**
 * Configures user-related routes.
 */
fun Route.userRoutes(userService: UserService) {
    route("/users") {
        
        // GET /users - Get all users
        get {
            try {
                val users = userService.getAllUsers()
                val userResponses = users.map { 
                    UserResponse(
                        userName = it.userName ?: "",
                        userEmail = it.userEmail ?: ""
                    )
                }
                call.respond(HttpStatusCode.OK, userResponses)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Failed to fetch users: ${e.message}")
                )
            }
        }
        
        // POST /users/register - Register a new user
        post("/register") {
            try {
                val request = call.receive<UserRegisterRequest>()
                val user = User(
                    userName = request.userName,
                    userEmail = request.userEmail,
                    userPassword = request.userPassword
                )
                
                userService.registerUser(user)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(true, "User registered successfully")
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(false, error.message)
                        )
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Registration failed: ${e.message}")
                )
            }
        }
        
        // PUT /users/password - Update user password
        put("/password") {
            try {
                val request = call.receive<UpdatePasswordRequest>()
                
                userService.updatePassword(request.userName, request.newPassword)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(true, "Password updated successfully")
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse(false, error.message)
                        )
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Password update failed: ${e.message}")
                )
            }
        }
        
        // DELETE /users/{username} - Delete user by username
        delete("/{username}") {
            try {
                val username = call.parameters["username"] ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, "Username parameter is required")
                    )
                    return@delete
                }
                
                userService.deleteUser(username)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(true, "User deleted successfully")
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse(false, error.message)
                        )
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, "Delete failed: ${e.message}")
                )
            }
        }
    }
}

