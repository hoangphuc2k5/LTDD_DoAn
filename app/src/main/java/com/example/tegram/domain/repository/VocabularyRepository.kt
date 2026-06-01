package com.example.tegram.domain.repository

import com.example.tegram.data.remote.dto.*
import com.example.tegram.domain.util.Resource
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface VocabularyRepository {
    // Personal
    suspend fun getVocabularies(page: Int, limit: Int, query: String?): Resource<VocabularyResponseDto>
    suspend fun createVocabulary(word: String, meaning: String, pronunciation: String?, example: String?, topic: String?, isPublic: Boolean): Resource<VocabularyDto>
    suspend fun updateVocabulary(id: String, word: String, meaning: String, pronunciation: String?, example: String?, topic: String?, isPublic: Boolean): Resource<VocabularyDto>
    suspend fun deleteVocabulary(id: String): Resource<Unit>
    suspend fun importCSV(file: MultipartBody.Part): Resource<Map<String, Any>>
    suspend fun exportCSV(): Resource<ResponseBody>

    // Public
    suspend fun getPublicVocabularies(page: Int, limit: Int, query: String?, topic: String?, sort: String?): Resource<PublicVocabularyResponseDto>
    suspend fun getPublicVocabularyDetails(id: String, page: Int, limit: Int): Resource<PublicVocabularyDetailsResponseDto>
    suspend fun saveToPersonal(id: String): Resource<Map<String, Any>>

    // Dictionary
    suspend fun lookupWord(word: String): Resource<List<Map<String, Any>>>
    suspend fun suggestWord(query: String): Resource<List<Map<String, Any>>>
}
