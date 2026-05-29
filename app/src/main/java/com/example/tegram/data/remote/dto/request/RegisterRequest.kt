package com.example.tegram.data.remote.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)