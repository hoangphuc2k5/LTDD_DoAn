package com.example.tegram.data.remote.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserSyncRequest(
    val uid: String,
    val fullName: String,
    val email: String,
    val provider: String,
    val photoUrl: String?,
    val isGoogleUser: Boolean,
    val syncedAt: Long
)