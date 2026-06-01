package com.example.tegram.data.mapper

import com.example.tegram.data.remote.dto.response.RemoteDailyPlanDto
import com.example.tegram.data.remote.dto.response.RemoteFlashcardDto
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun RemoteFlashcardDto.toDomain(): Flashcard =
	Flashcard(
		id = id,
		term = term,
		pronunciation = pronunciation,
		meaning = meaning,
		example = example,
		topic = topic
	)

fun RemoteFlashcardDto.toSchedule(): ReviewSchedule =
	ReviewSchedule(
		cardId = id,
		repetitions = repetitions,
		intervalDays = intervalDays,
		easeFactor = easeFactor,
		dueEpochDay = dueAt.toEpochDayOrToday(),
		lastReviewedEpochDay = lastReviewedAt?.toEpochDayOrToday()
	)

fun RemoteDailyPlanDto.toDomain(): DailyPlan =
	DailyPlan(
		totalCards = totalCards,
		newCards = newCards,
		dueCards = dueCards,
		overdueCards = overdueCards,
		estimatedMinutes = estimatedMinutes,
		nextDueEpochDay = nextDueAt?.toEpochDayOrToday()
	)

private fun String?.toEpochDayOrToday(): Long {
	if (this.isNullOrBlank()) return todayEpochDay()

	val date = runCatching { isoDateFormat.parse(this) }.getOrNull()
	return date?.time?.let { todayEpochDay(it) } ?: todayEpochDay()
}

private val isoDateFormat: SimpleDateFormat
	get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
		timeZone = TimeZone.getTimeZone("UTC")
	}
