package com.example.tegram.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PublicVocabularyDto(
    @Json(name = "_id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "topic") val topic: String,
    @Json(name = "level") val level: String,
    @Json(name = "totalWords") val totalWords: Int,
    @Json(name = "downloads") val downloads: Int,
    @Json(name = "createdAt") val createdAt: String? = null
)
