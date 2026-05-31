package com.example.tegram.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PublicWordDto(
    @Json(name = "_id") val id: String,
    @Json(name = "publicVocabularyId") val publicVocabularyId: String,
    @Json(name = "word") val word: String,
    @Json(name = "meaning") val meaning: String,
    @Json(name = "pronunciation") val pronunciation: String? = null,
    @Json(name = "example") val example: String? = null
)
