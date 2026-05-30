package com.example.tegram.data.remote.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateFlashcardRequest(
	val userId: String,
	val term: String,
	val pronunciation: String = "",
	val meaning: String,
	val example: String = "",
	val topic: String = "General"
)

@JsonClass(generateAdapter = true)
data class ReviewRequest(
	val userId: String,
	val cardId: String,
	val rating: String
)

@JsonClass(generateAdapter = true)
data class SeedFlashcardsRequest(
	val userId: String,
	val force: Boolean = false
)
