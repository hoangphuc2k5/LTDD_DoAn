package com.example.tegram.data.repository

import com.example.tegram.data.remote.api.VocabularyApiService
import com.example.tegram.data.remote.dto.*
import com.example.tegram.domain.repository.VocabularyRepository
import com.example.tegram.domain.util.Resource
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class VocabularyRepositoryImpl(
    private val apiService: VocabularyApiService
) : VocabularyRepository {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error(response.message() ?: "An error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not connect to server")
        }
    }

    override suspend fun getVocabularies(
        page: Int,
        limit: Int,
        query: String?
    ): Resource<VocabularyResponseDto> {
        return safeApiCall { apiService.getVocabularies(page, limit, query) }
    }

    override suspend fun createVocabulary(
        word: String,
        meaning: String,
        pronunciation: String?,
        example: String?,
        topic: String?,
        isPublic: Boolean
    ): Resource<VocabularyDto> {
        val body = mapOf(
            "word" to word,
            "meaning" to meaning,
            "pronunciation" to (pronunciation ?: ""),
            "example" to (example ?: ""),
            "topic" to (topic ?: ""),
            "isPublic" to isPublic
        )
        return safeApiCall { apiService.createVocabulary(body) }
    }

    override suspend fun updateVocabulary(
        id: String,
        word: String,
        meaning: String,
        pronunciation: String?,
        example: String?,
        topic: String?,
        isPublic: Boolean
    ): Resource<VocabularyDto> {
        val body = mapOf(
            "word" to word,
            "meaning" to meaning,
            "pronunciation" to (pronunciation ?: ""),
            "example" to (example ?: ""),
            "topic" to (topic ?: ""),
            "isPublic" to isPublic
        )
        return safeApiCall { apiService.updateVocabulary(id, body) }
    }

    override suspend fun deleteVocabulary(id: String): Resource<Unit> {
        return try {
            val response = apiService.deleteVocabulary(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "An error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not connect to server")
        }
    }

    override suspend fun importCSV(file: MultipartBody.Part): Resource<Map<String, Any>> {
        return safeApiCall { apiService.importCSV(file) }
    }

    override suspend fun exportCSV(): Resource<ResponseBody> {
        return try {
            val response = apiService.exportCSV()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "An error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not connect to server")
        }
    }

    override suspend fun getPublicVocabularies(
        page: Int,
        limit: Int,
        query: String?,
        topic: String?,
        sort: String?
    ): Resource<PublicVocabularyResponseDto> {
        return safeApiCall { apiService.getPublicVocabularies(page, limit, query, topic, sort) }
    }

    override suspend fun getPublicVocabularyDetails(
        id: String,
        page: Int,
        limit: Int
    ): Resource<PublicVocabularyDetailsResponseDto> {
        return safeApiCall { apiService.getPublicVocabularyDetails(id, page, limit) }
    }

    override suspend fun saveToPersonal(id: String): Resource<Map<String, Any>> {
        return safeApiCall { apiService.saveToPersonal(id) }
    }

    override suspend fun lookupWord(word: String): Resource<List<Map<String, Any>>> {
        return safeApiCall { apiService.lookupWord(word) }
    }

    override suspend fun suggestWord(query: String): Resource<List<Map<String, Any>>> {
        return safeApiCall { apiService.suggestWord(query) }
    }
}
