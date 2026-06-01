package com.example.tegram.data.repository

import com.example.tegram.data.mapper.toDomain
import com.example.tegram.data.mapper.toSchedule
import com.example.tegram.data.remote.api.LearningApiService
import com.example.tegram.data.remote.dto.request.CreateFlashcardRequest
import com.example.tegram.data.remote.dto.request.ReviewRequest
import com.example.tegram.data.remote.dto.request.SeedFlashcardsRequest
import com.example.tegram.data.remote.dto.response.RemoteFlashcardDto
import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.repository.LearningRepository
import com.example.tegram.domain.repository.LearningSnapshot
import com.example.tegram.domain.usecase.learning.GetDailyPlanUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LearningRepositoryImpl(
	private val learningApiService: LearningApiService,
	private val dailyPlanUseCase: GetDailyPlanUseCase
) : LearningRepository {

	override suspend fun loadLearning(userId: String): LearningSnapshot = withContext(Dispatchers.IO) {
		val response = learningApiService.getFlashcards(userId)
		if (!response.success) {
			error(response.message ?: "Không tải được flashcard từ backend")
		}

		val cards = if (response.flashcards.isEmpty()) {
			learningApiService.seedFlashcards(SeedFlashcardsRequest(userId)).flashcards
		} else {
			response.flashcards
		}

		cards.toSnapshot(userId)
	}

	override suspend fun seedFlashcards(userId: String, force: Boolean): LearningSnapshot = withContext(Dispatchers.IO) {
		val response = learningApiService.seedFlashcards(SeedFlashcardsRequest(userId, force))
		if (!response.success) {
			error(response.message ?: "Không tạo được dữ liệu mẫu")
		}

		response.flashcards.toSnapshot(userId)
	}

	override suspend fun createFlashcard(
		userId: String,
		term: String,
		pronunciation: String,
		meaning: String,
		example: String,
		topic: String
	): LearningSnapshot = withContext(Dispatchers.IO) {
		val response = learningApiService.createFlashcard(
			CreateFlashcardRequest(
				userId = userId,
				term = term,
				pronunciation = pronunciation,
				meaning = meaning,
				example = example,
				topic = topic
			)
		)
		if (!response.success || response.flashcard == null) {
			error(response.message ?: "Không thêm được flashcard")
		}

		loadLearning(userId)
	}

	override suspend fun deleteFlashcard(userId: String, cardId: String): LearningSnapshot = withContext(Dispatchers.IO) {
		val response = learningApiService.deleteFlashcard(cardId, userId)
		if (!response.success) {
			error(response.message ?: "Không xóa được flashcard")
		}

		loadLearning(userId)
	}

	override suspend fun submitReview(
		userId: String,
		cardId: String,
		rating: ReviewRating
	): LearningSnapshot = withContext(Dispatchers.IO) {
		val response = learningApiService.submitReview(
			ReviewRequest(
				userId = userId,
				cardId = cardId,
				rating = rating.label
			)
		)
		if (!response.success || response.flashcard == null) {
			error(response.message ?: "Không cập nhật được lịch ôn")
		}

		loadLearning(userId)
	}

	private suspend fun List<RemoteFlashcardDto>.toSnapshot(userId: String): LearningSnapshot {
		val schedules = associate { dto -> dto.id to dto.toSchedule() }
		val remotePlan = runCatching { learningApiService.getDailyPlan(userId).plan?.toDomain() }.getOrNull()
		return LearningSnapshot(
			cards = map { it.toDomain() },
			schedules = schedules,
			dailyPlan = remotePlan ?: dailyPlanUseCase(schedules.values)
		)
	}
}
