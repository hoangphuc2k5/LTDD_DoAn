package com.example.tegram.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = userRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    suspend fun loginWithEmail(email: String, password: String): UserProfile =
        userRepository.loginWithEmail(email, password)

    suspend fun registerWithEmail(fullName: String, email: String, password: String): UserProfile =
        userRepository.registerWithEmail(fullName, email, password)

    suspend fun loginWithGoogle(fullName: String?, email: String, photoUrl: String?): UserProfile =
        userRepository.loginWithGoogle(fullName, email, photoUrl)

    suspend fun logout() {
        userRepository.logout()
    }
}