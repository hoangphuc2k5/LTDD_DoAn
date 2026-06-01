package com.example.tegram.data.remote.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    val fullName: String? = null,
    val photoUrl: String? = null
)
