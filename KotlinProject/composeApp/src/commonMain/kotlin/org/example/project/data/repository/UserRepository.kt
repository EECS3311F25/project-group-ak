package org.example.project.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.model.dataClasses.User
import org.example.project.data.remote.RemoteUserDataSource

class UserRepository(
    private val remoteUserDataSource: RemoteUserDataSource
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()
    
    suspend fun getCurrentUser(): User {
        val user = remoteUserDataSource.getCurrentUser()
        _currentUser.value = user
        return user
    }
    
    suspend fun getAllUsers(): List<User> {
        val users = remoteUserDataSource.getAllUsers()
        _allUsers.value = users
        return users
    }
    
    suspend fun getUserById(id: String): User? {
        return remoteUserDataSource.getUserById(id)
    }
}