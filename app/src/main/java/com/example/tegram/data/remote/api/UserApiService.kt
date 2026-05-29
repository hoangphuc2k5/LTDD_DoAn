package com.example.tegram.data.remote.api

import com.example.tegram.data.remote.dto.request.LoginRequest
import com.example.tegram.data.remote.dto.request.RegisterRequest
import com.example.tegram.data.remote.dto.request.UserSyncRequest
import com.example.tegram.data.remote.dto.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
	@POST("auth/login")
	suspend fun login(@Body request: LoginRequest): AuthResponse

	@POST("auth/register")
	suspend fun register(@Body request: RegisterRequest): AuthResponse

	@POST("users/sync")
	suspend fun syncUser(@Body request: UserSyncRequest): AuthResponse
}
