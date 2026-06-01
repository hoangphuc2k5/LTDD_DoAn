package com.example.tegram

import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.usecase.learning.Sm2SchedulerUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm2SchedulerUseCaseTest {

	private val scheduler = Sm2SchedulerUseCase()

	@Test
	fun goodRatingSchedulesFirstReviewForTomorrow() {
		val today = 20_000L
		val result = scheduler(
			schedule = ReviewSchedule(cardId = "card-1", dueEpochDay = today),
			rating = ReviewRating.Good,
			todayEpochDay = today
		)

		assertEquals(1, result.repetitions)
		assertEquals(1, result.intervalDays)
		assertEquals(today + 1, result.dueEpochDay)
		assertEquals(today, result.lastReviewedEpochDay)
	}

	@Test
	fun againRatingResetsRepetitionAndKeepsCardDueToday() {
		val today = 20_000L
		val result = scheduler(
			schedule = ReviewSchedule(
				cardId = "card-1",
				repetitions = 3,
				intervalDays = 10,
				dueEpochDay = today
			),
			rating = ReviewRating.Again,
			todayEpochDay = today
		)

		assertEquals(0, result.repetitions)
		assertEquals(0, result.intervalDays)
		assertEquals(today, result.dueEpochDay)
	}

	@Test
	fun hardRatingNeverDropsEaseBelowMinimum() {
		val today = 20_000L
		val result = scheduler(
			schedule = ReviewSchedule(
				cardId = "card-1",
				repetitions = 5,
				intervalDays = 20,
				easeFactor = 1.31,
				dueEpochDay = today
			),
			rating = ReviewRating.Hard,
			todayEpochDay = today
		)

		assertTrue(result.easeFactor >= 1.3)
		assertTrue(result.intervalDays >= 1)
	}
}
