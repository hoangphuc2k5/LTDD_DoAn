package com.example.tegram.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tegram.data.local.dao.DailyProgressDao
import com.example.tegram.data.local.entity.DailyProgressEntity
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dailyProgressDao: DailyProgressDao
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = userRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentProgress: StateFlow<List<DailyProgressEntity>> = dailyProgressDao.observeRecentProgress(7)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                if (user != null) {
                    // Check if daily progress database is empty. If yes, seed mock data.
                    val currentList = dailyProgressDao.observeRecentProgress(7).first()
                    if (currentList.isEmpty()) {
                        seedMockData(user.uid)
                    }
                }
            }
        }
    }

    private suspend fun seedMockData(userId: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // 7 days of historical study statistics
        val mockReviews = listOf(45, 30, 55, 40, 60, 50, 65)
        val mockCorrect = listOf(38, 25, 48, 36, 52, 45, 58)
        val mockLearned = listOf(15, 10, 20, 12, 22, 18, 25)

        for (i in 6 downTo 0) {
            val tempCal = calendar.clone() as Calendar
            tempCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateString = formatter.format(tempCal.time)
            dailyProgressDao.upsert(
                DailyProgressEntity(
                    date = dateString,
                    wordsLearnedCount = mockLearned[6 - i],
                    reviewsCount = mockReviews[6 - i],
                    correctReviewsCount = mockCorrect[6 - i]
                )
            )
        }

        // Seeding user stats
        userRepository.updateUserProgress(
            streak = 5,
            level = "B1",
            wordsLearned = 122,
            totalReviews = 345,
            correctReviews = 302
        )
    }

    /**
     * Simulates learning a new word. Increments words learned and reviews.
     * Calculates the new CEFR level based on total words learned.
     */
    fun simulateLearnWord(isCorrect: Boolean) {
        viewModelScope.launch {
            val user = userRepository.currentUser.first() ?: return@launch
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = formatter.format(Date())

            val todayProgress = dailyProgressDao.getProgressForDate(todayStr) 
                ?: DailyProgressEntity(todayStr, 0, 0, 0)
            
            val updatedProgress = todayProgress.copy(
                wordsLearnedCount = todayProgress.wordsLearnedCount + if (isCorrect) 1 else 0,
                reviewsCount = todayProgress.reviewsCount + 1,
                correctReviewsCount = todayProgress.correctReviewsCount + if (isCorrect) 1 else 0
            )
            dailyProgressDao.upsert(updatedProgress)

            val totalReviews = user.totalReviews + 1
            val correctReviews = user.correctReviews + if (isCorrect) 1 else 0
            val wordsLearned = user.wordsLearned + if (isCorrect) 1 else 0

            // CEFR vocabulary estimation:
            // A1: 0 - 50, A2: 51 - 100, B1: 101 - 200, B2: 201 - 500, C1: 501 - 1000, C2: >1000
            val newLevel = when {
                wordsLearned <= 50 -> "A1"
                wordsLearned <= 100 -> "A2"
                wordsLearned <= 200 -> "B1"
                wordsLearned <= 500 -> "B2"
                wordsLearned <= 1000 -> "C1"
                else -> "C2"
            }

            userRepository.updateUserProgress(
                streak = if (user.streak == 0) 1 else user.streak,
                level = newLevel,
                wordsLearned = wordsLearned,
                totalReviews = totalReviews,
                correctReviews = correctReviews
            )
        }
    }
}
