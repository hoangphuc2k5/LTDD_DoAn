package com.example.tegram.domain.repository

import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.model.learning.ReviewSchedule

data class LearningSnapshot(
	val cards: List<Flashcard>,
	val schedules: Map<String, ReviewSchedule>,
	val dailyPlan: DailyPlan
)

interface LearningRepository {
	suspend fun loadLearning(userId: String): LearningSnapshot

	suspend fun seedFlashcards(userId: String, force: Boolean = false): LearningSnapshot

	suspend fun createFlashcard(
		userId: String,
		term: String,
		pronunciation: String,
		meaning: String,
		example: String,
		topic: String
	): LearningSnapshot

	suspend fun deleteFlashcard(userId: String, cardId: String): LearningSnapshot

	suspend fun submitReview(userId: String, cardId: String, rating: ReviewRating): LearningSnapshot
}
