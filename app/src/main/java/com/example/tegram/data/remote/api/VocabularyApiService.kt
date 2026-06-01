package com.example.tegram.data.remote.api

import com.example.tegram.data.remote.dto.PublicVocabularyDetailsResponseDto
import com.example.tegram.data.remote.dto.PublicVocabularyResponseDto
import com.example.tegram.data.remote.dto.VocabularyDto
import com.example.tegram.data.remote.dto.VocabularyResponseDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface VocabularyApiService {

    // --- Personal Vocabulary ---
    @GET("api/vocabulary")
    suspend fun getVocabularies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("q") query: String? = null
    ): Response<VocabularyResponseDto>

    @POST("api/vocabulary")
    suspend fun createVocabulary(
        @Body request: Map<String, Any>
    ): Response<VocabularyDto>

    @PUT("api/vocabulary/{id}")
    suspend fun updateVocabulary(
        @Path("id") id: String,
        @Body request: Map<String, Any>
    ): Response<VocabularyDto>

    @DELETE("api/vocabulary/{id}")
    suspend fun deleteVocabulary(
        @Path("id") id: String
    ): Response<Unit>

    @Multipart
    @POST("api/vocabulary/import")
    suspend fun importCSV(
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>

    @Streaming
    @GET("api/vocabulary/export")
    suspend fun exportCSV(): Response<ResponseBody>


    // --- Public Vocabulary ---
    @GET("api/public-vocabulary")
    suspend fun getPublicVocabularies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("q") query: String? = null,
        @Query("topic") topic: String? = null,
        @Query("sort") sort: String? = null
    ): Response<PublicVocabularyResponseDto>

    @GET("api/public-vocabulary/{id}")
    suspend fun getPublicVocabularyDetails(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<PublicVocabularyDetailsResponseDto>

    @POST("api/public-vocabulary/{id}/save")
    suspend fun saveToPersonal(
        @Path("id") id: String
    ): Response<Map<String, Any>>


    // --- Dictionary Proxy API ---
    @GET("api/dictionary/lookup/{word}")
    suspend fun lookupWord(
        @Path("word") word: String
    ): Response<List<Map<String, Any>>>

    @GET("api/dictionary/suggest")
    suspend fun suggestWord(
        @Query("q") query: String
    ): Response<List<Map<String, Any>>>
}
