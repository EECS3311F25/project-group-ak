package org.example.project

import org.example.project.userModel.UserRepository

class TempUserRepository: UserRepository {
    private var users = listOf(
        User("user100", "user100@gmail.com", "password0"),
        User("user101", "user101@gmail.com", "password1"),
        User("user102", "user102@gmail.com", "password2"),
        User("user103", "user103@gmail.com", "password3"),
        User("user104", "user104@gmail.com", "password4")
        )

    override suspend fun allUsers(): List<User> = users

    override suspend fun addUser(user: User) {
        //  TODO: implement exception handling for user that already exists / bad input

        users.plus(user)
    }

    //  TODO: implement method
    override suspend fun updateUserPassword(userName: String?, newPassword: String) {}

    override suspend fun deleteUserByUsername(userName: String?): Boolean {
        val oldUsers = users
        users = users.filterNot { it.userName == userName }
        return oldUsers.size > users.size
    }
}