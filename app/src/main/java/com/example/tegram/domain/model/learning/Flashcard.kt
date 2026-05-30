package com.example.tegram.domain.model.learning

data class Flashcard(
	val id: String,
	val term: String,
	val pronunciation: String,
	val meaning: String,
	val example: String,
	val topic: String
)

enum class ReviewRating(val quality: Int, val label: String, val helper: String) {
	Again(1, "Again", "Quên, cần học lại ngay"),
	Hard(3, "Hard", "Nhớ khó, ôn lại sớm"),
	Good(4, "Good", "Nhớ ổn"),
	Easy(5, "Easy", "Nhớ chắc, giãn lịch")
}

data class ReviewSchedule(
	val cardId: String,
	val repetitions: Int = 0,
	val intervalDays: Int = 0,
	val easeFactor: Double = 2.5,
	val dueEpochDay: Long = todayEpochDay(),
	val lastReviewedEpochDay: Long? = null
) {
	fun isNew(): Boolean = lastReviewedEpochDay == null
	fun isDue(todayEpochDay: Long = todayEpochDay()): Boolean = dueEpochDay <= todayEpochDay
}

data class DailyPlan(
	val totalCards: Int,
	val newCards: Int,
	val dueCards: Int,
	val overdueCards: Int,
	val estimatedMinutes: Int,
	val nextDueEpochDay: Long?
)

fun todayEpochDay(currentTimeMillis: Long = System.currentTimeMillis()): Long =
	currentTimeMillis / MILLIS_PER_DAY

fun epochDayToMillis(epochDay: Long): Long = epochDay * MILLIS_PER_DAY

private const val MILLIS_PER_DAY = 86_400_000L
