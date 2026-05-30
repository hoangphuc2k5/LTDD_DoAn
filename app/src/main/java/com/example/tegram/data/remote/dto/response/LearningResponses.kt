package com.example.tegram.data.remote.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlashcardListResponse(
	val success: Boolean = true,
	val message: String? = null,
	val flashcards: List<RemoteFlashcardDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FlashcardResponse(
	val success: Boolean = true,
	val message: String? = null,
	val flashcard: RemoteFlashcardDto? = null
)

@JsonClass(generateAdapter = true)
data class ReviewResponse(
	val success: Boolean = true,
	val message: String? = null,
	val rating: String? = null,
	val flashcard: RemoteFlashcardDto? = null,
	val schedule: RemoteScheduleDto? = null
)

@JsonClass(generateAdapter = true)
data class DailyPlanResponse(
	val success: Boolean = true,
	val message: String? = null,
	val plan: RemoteDailyPlanDto? = null,
	val dueCards: List<RemoteFlashcardDto> = emptyList(),
	val upcomingCards: List<RemoteFlashcardDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RemoteFlashcardDto(
	val id: String,
	val userId: String,
	val term: String,
	val pronunciation: String = "",
	val meaning: String,
	val example: String = "",
	val topic: String = "General",
	val repetitions: Int = 0,
	val intervalDays: Int = 0,
	val easeFactor: Double = 2.5,
	val dueAt: String? = null,
	val lastReviewedAt: String? = null,
	val createdAt: String? = null,
	val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteScheduleDto(
	val repetitions: Int = 0,
	val intervalDays: Int = 0,
	val easeFactor: Double = 2.5,
	val dueAt: String? = null,
	val lastReviewedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteDailyPlanDto(
	val totalCards: Int = 0,
	val newCards: Int = 0,
	val dueCards: Int = 0,
	val overdueCards: Int = 0,
	val estimatedMinutes: Int = 0,
	val nextDueAt: String? = null
)
