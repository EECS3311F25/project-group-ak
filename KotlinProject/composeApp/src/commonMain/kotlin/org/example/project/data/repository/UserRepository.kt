package org.example.project.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.model.User
import org.example.project.data.source.UserDataSource

class UserRepository(
    private val userDataSource: UserDataSource
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()
    
    suspend fun getCurrentUser(): User {
        val user = userDataSource.getCurrentUser()
        _currentUser.value = user
        return user
    }
    
    suspend fun getAllUsers(): List<User> {
        val users = userDataSource.getAllUsers()
        _allUsers.value = users
        return users
    }
    
    suspend fun getUserById(id: String): User? {
        return userDataSource.getUserById(id)
    }
}