package com.example.tegram.domain.repository

import com.example.tegram.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
	val currentUser: Flow<UserProfile?>

	suspend fun loginWithEmail(email: String, password: String): UserProfile

	suspend fun registerWithEmail(fullName: String, email: String, password: String): UserProfile

	suspend fun loginWithGoogle(fullName: String?, email: String, photoUrl: String?): UserProfile

	suspend fun logout()
}
