package com.example.tegram.data.remote.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
	val success: Boolean = true,
	val message: String? = null,
	val token: String? = null,
	val user: RemoteUserDto? = null
)

@JsonClass(generateAdapter = true)
data class RemoteUserDto(
	val uid: String,
	val fullName: String,
	val email: String,
	val provider: String,
	val photoUrl: String? = null,
	val isGoogleUser: Boolean = false,
	val streak: Int = 0,
	val level: String = "A1",
	val wordsLearned: Int = 0,
	val totalReviews: Int = 0,
	val correctReviews: Int = 0,
	val syncedAt: Long = System.currentTimeMillis()
)
