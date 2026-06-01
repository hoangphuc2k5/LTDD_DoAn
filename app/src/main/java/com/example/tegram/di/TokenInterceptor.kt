package com.example.tegram.di

import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
	private val userPreferencesDataStore: UserPreferencesDataStore
) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val originalRequest = chain.request()
		
		// Get token synchronously from DataStore
		val token = runBlocking {
			userPreferencesDataStore.authTokenFlow.firstOrNull()
		}
		
		// If token exists, add it to Authorization header
		val request = if (!token.isNullOrBlank()) {
			originalRequest.newBuilder()
				.header("Authorization", "Bearer $token")
				.build()
		} else {
			originalRequest
		}
		
		return chain.proceed(request)
	}
}
