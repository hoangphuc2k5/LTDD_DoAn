package com.example.tegram.data.remote.api

import com.example.tegram.data.remote.dto.request.CreateFlashcardRequest
import com.example.tegram.data.remote.dto.request.ReviewRequest
import com.example.tegram.data.remote.dto.request.SeedFlashcardsRequest
import com.example.tegram.data.remote.dto.response.DailyPlanResponse
import com.example.tegram.data.remote.dto.response.FlashcardListResponse
import com.example.tegram.data.remote.dto.response.FlashcardResponse
import com.example.tegram.data.remote.dto.response.ReviewResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LearningApiService {
	@GET("learning/flashcards")
	suspend fun getFlashcards(@Query("userId") userId: String): FlashcardListResponse

	@POST("learning/flashcards")
	suspend fun createFlashcard(@Body request: CreateFlashcardRequest): FlashcardResponse

	@PATCH("learning/flashcards/{id}")
	suspend fun updateFlashcard(
		@Path("id") id: String,
		@Body request: CreateFlashcardRequest
	): FlashcardResponse

	@DELETE("learning/flashcards/{id}")
	suspend fun deleteFlashcard(
		@Path("id") id: String,
		@Query("userId") userId: String
	): FlashcardResponse

	@GET("learning/review")
	suspend fun getDueReview(@Query("userId") userId: String): FlashcardListResponse

	@POST("learning/review")
	suspend fun submitReview(@Body request: ReviewRequest): ReviewResponse

	@GET("learning/daily-plan")
	suspend fun getDailyPlan(@Query("userId") userId: String): DailyPlanResponse

	@POST("learning/seed")
	suspend fun seedFlashcards(@Body request: SeedFlashcardsRequest): FlashcardListResponse
}
