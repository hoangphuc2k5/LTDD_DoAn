package com.example.tegram.domain.usecase.learning

import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay
import kotlin.math.roundToInt

class Sm2SchedulerUseCase {

	operator fun invoke(
		schedule: ReviewSchedule,
		rating: ReviewRating,
		todayEpochDay: Long = todayEpochDay()
	): ReviewSchedule {
		val quality = rating.quality
		val nextEaseFactor = calculateEaseFactor(schedule.easeFactor, quality)

		if (quality < MIN_PASSING_QUALITY) {
			return schedule.copy(
				repetitions = 0,
				intervalDays = 0,
				easeFactor = nextEaseFactor,
				dueEpochDay = todayEpochDay,
				lastReviewedEpochDay = todayEpochDay
			)
		}

		val nextRepetitions = schedule.repetitions + 1
		val nextInterval = when (nextRepetitions) {
			1 -> 1
			2 -> 6
			else -> (schedule.intervalDays * nextEaseFactor).roundToInt().coerceAtLeast(1)
		}

		return schedule.copy(
			repetitions = nextRepetitions,
			intervalDays = nextInterval,
			easeFactor = nextEaseFactor,
			dueEpochDay = todayEpochDay + nextInterval,
			lastReviewedEpochDay = todayEpochDay
		)
	}

	private fun calculateEaseFactor(currentEaseFactor: Double, quality: Int): Double {
		val qualityGap = 5 - quality
		val nextEaseFactor = currentEaseFactor + (0.1 - qualityGap * (0.08 + qualityGap * 0.02))
		return nextEaseFactor.coerceAtLeast(MIN_EASE_FACTOR)
	}

	private companion object {
		const val MIN_PASSING_QUALITY = 3
		const val MIN_EASE_FACTOR = 1.3
	}
}
