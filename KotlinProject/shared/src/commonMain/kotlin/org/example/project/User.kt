package org.example.project

//  import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//  every serializable class must be marked @Serializable

@Serializable
data class User (
    //  @SerialName("user_name")
    val userName: String?,
    //  @SerialName("user_email")
    val userEmail: String?,
    //  @SerialName("user_password")
    val userPassword: String?) {
}