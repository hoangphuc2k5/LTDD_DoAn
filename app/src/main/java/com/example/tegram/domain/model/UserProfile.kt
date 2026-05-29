package com.example.tegram.domain.model

data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val provider: String,
    val photoUrl: String? = null,
    val isGoogleUser: Boolean = false,
    val passwordHash: String? = null,
    val passwordSalt: String? = null,
    val syncedAt: Long = System.currentTimeMillis()
)