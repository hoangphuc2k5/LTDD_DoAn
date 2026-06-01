package com.example.tegram.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VocabularyResponseDto(
    @Json(name = "data") val data: List<VocabularyDto>,
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class PublicVocabularyResponseDto(
    @Json(name = "data") val data: List<PublicVocabularyDto>,
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class PublicWordResponseDto(
    @Json(name = "data") val data: List<PublicWordDto>,
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class PublicVocabularyDetailsResponseDto(
    @Json(name = "collection") val collection: PublicVocabularyDto,
    @Json(name = "words") val words: PublicWordResponseDto
)
