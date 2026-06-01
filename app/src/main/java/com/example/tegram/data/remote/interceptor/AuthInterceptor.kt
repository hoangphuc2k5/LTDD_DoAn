package com.example.tegram.data.remote.interceptor

import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferencesDataStore.authTokenFlow.first()
        }

        val request = chain.request().newBuilder()
        
        if (!token.isNullOrBlank()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}
