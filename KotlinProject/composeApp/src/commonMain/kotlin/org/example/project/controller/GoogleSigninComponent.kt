package org.example.project.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//  Google sign-in data
data class AuthUiState(
    val errorMessage: String? = null,
    val isProcessing: Boolean = false,
    val user: String? = null,
)


class GoogleSigninComponent (
    private val googleAuthManager: GoogleAuthManager)
{
    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState = _authUiState.asStateFlow()


//    fun signInWithGoogle() {
//        viewModelScope.launch {
//            try {
//                googleAuthManager.signIn { kmaAuthUser, error ->
//                    if(error == null && kmaAuthUser != null) {
//                        val user = kmaAuthUser.name
//                        _authUiState.update {
//                            it.copy(
//                                user = user
//                            )
//                        }
//                    } else {
//                        error?.printStackTrace()
//                        _authUiState.update {
//                            it.copy(
//                                user = error?.message
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                print(e.printStackTrace())
//                _authUiState.update {
//                    it.copy(
//                        user = e.message
//                    )
//                }
//            }
//        }
//    }
//
//    fun signOut() {
//        viewModelScope.launch {
//            googleAuthManager.signOut()
//            _authUiState.update {
//                it.copy(
//                    user = null
//                )
//            }
//        }
//    }
//
}