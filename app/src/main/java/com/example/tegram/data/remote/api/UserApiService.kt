package com.example.tegram.data.remote.api

import com.example.tegram.data.remote.dto.request.LoginRequest
import com.example.tegram.data.remote.dto.request.RegisterRequest
import com.example.tegram.data.remote.dto.request.UpdateUserRequest
import com.example.tegram.data.remote.dto.request.UserSyncRequest
import com.example.tegram.data.remote.dto.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
	@POST("auth/login")
	suspend fun login(@Body request: LoginRequest): AuthResponse

	@POST("auth/register")
	suspend fun register(@Body request: RegisterRequest): AuthResponse

	@POST("users/sync")
	suspend fun syncUser(@Body request: UserSyncRequest): AuthResponse

	@PUT("users/{uid}")
	suspend fun updateProfile(
		@Path("uid") uid: String,
		@Body request: UpdateUserRequest
	): AuthResponse
}
