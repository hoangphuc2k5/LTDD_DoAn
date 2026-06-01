package com.example.tegram.domain.usecase.learning

import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay

class GetDailyPlanUseCase {

	operator fun invoke(
		schedules: Collection<ReviewSchedule>,
		todayEpochDay: Long = todayEpochDay()
	): DailyPlan {
		val newCards = schedules.count { it.isNew() }
		val dueCards = schedules.count { it.isDue(todayEpochDay) }
		val overdueCards = schedules.count { it.dueEpochDay < todayEpochDay }
		val nextDueEpochDay = schedules
			.filter { it.dueEpochDay > todayEpochDay }
			.minOfOrNull { it.dueEpochDay }

		return DailyPlan(
			totalCards = schedules.size,
			newCards = newCards,
			dueCards = dueCards,
			overdueCards = overdueCards,
			estimatedMinutes = (dueCards * MINUTES_PER_REVIEW_CARD + newCards * MINUTES_PER_NEW_CARD)
				.coerceAtLeast(if (schedules.isEmpty()) 0 else MIN_SESSION_MINUTES),
			nextDueEpochDay = nextDueEpochDay
		)
	}

	private companion object {
		const val MINUTES_PER_REVIEW_CARD = 2
		const val MINUTES_PER_NEW_CARD = 3
		const val MIN_SESSION_MINUTES = 5
	}
}
