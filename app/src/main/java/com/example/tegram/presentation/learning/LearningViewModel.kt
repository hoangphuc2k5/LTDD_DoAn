package com.example.tegram.presentation.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.repository.LearningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LearningUiState(
	val cards: List<Flashcard> = emptyList(),
	val schedules: Map<String, ReviewSchedule> = emptyMap(),
	val dailyPlan: DailyPlan = DailyPlan(
		totalCards = 0,
		newCards = 0,
		dueCards = 0,
		overdueCards = 0,
		estimatedMinutes = 0,
		nextDueEpochDay = null
	),
	val isLoading: Boolean = false,
	val errorMessage: String? = null
)

@HiltViewModel
class LearningViewModel @Inject constructor(
	private val learningRepository: LearningRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(LearningUiState())
	val uiState: StateFlow<LearningUiState> = _uiState

	private var currentUserId: String? = null

	fun load(userId: String?) {
		val normalizedUserId = userId?.trim().orEmpty()
		if (normalizedUserId.isBlank()) {
			currentUserId = null
			_uiState.value = LearningUiState()
			return
		}

		if (currentUserId == normalizedUserId && _uiState.value.cards.isNotEmpty()) {
			return
		}

		currentUserId = normalizedUserId
		refresh()
	}

	fun refresh() {
		val userId = currentUserId ?: return
		viewModelScope.launch {
			runCatching {
				_uiState.update { it.copy(isLoading = true, errorMessage = null) }
				learningRepository.loadLearning(userId)
			}.onSuccess { snapshot ->
				_uiState.value = LearningUiState(
					cards = snapshot.cards,
					schedules = snapshot.schedules,
					dailyPlan = snapshot.dailyPlan
				)
			}.onFailure { error ->
				_uiState.update {
					it.copy(
						isLoading = false,
						errorMessage = error.message ?: "Không tải được dữ liệu học tập"
					)
				}
			}
		}
	}

	fun seed(force: Boolean = false) {
		val userId = currentUserId ?: return
		viewModelScope.launch {
			runLearningAction {
				learningRepository.seedFlashcards(userId, force)
			}
		}
	}

	fun createFlashcard(
		term: String,
		pronunciation: String,
		meaning: String,
		example: String,
		topic: String
	) {
		val userId = currentUserId ?: return
		viewModelScope.launch {
			runLearningAction {
				learningRepository.createFlashcard(
					userId = userId,
					term = term.trim(),
					pronunciation = pronunciation.trim(),
					meaning = meaning.trim(),
					example = example.trim(),
					topic = topic.trim().ifBlank { "General" }
				)
			}
		}
	}

	fun deleteFlashcard(cardId: String) {
		val userId = currentUserId ?: return
		viewModelScope.launch {
			runLearningAction {
				learningRepository.deleteFlashcard(userId, cardId)
			}
		}
	}

	fun submitReview(cardId: String, rating: ReviewRating) {
		val userId = currentUserId ?: return
		viewModelScope.launch {
			runLearningAction {
				learningRepository.submitReview(userId, cardId, rating)
			}
		}
	}

	fun clearError() {
		_uiState.update { it.copy(errorMessage = null) }
	}

	private suspend fun runLearningAction(
		action: suspend () -> com.example.tegram.domain.repository.LearningSnapshot
	) {
		runCatching {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			action()
		}.onSuccess { snapshot ->
			_uiState.value = LearningUiState(
				cards = snapshot.cards,
				schedules = snapshot.schedules,
				dailyPlan = snapshot.dailyPlan
			)
		}.onFailure { error ->
			_uiState.update {
				it.copy(
					isLoading = false,
					errorMessage = error.message ?: "Không cập nhật được dữ liệu học tập"
				)
			}
		}
	}
}
