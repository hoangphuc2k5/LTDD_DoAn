package com.example.tegram.domain.usecase.learning

import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay

object LearningSeedData {
	val cards = listOf(
		Flashcard(
			id = "vocab-clarify",
			term = "clarify",
			pronunciation = "/ˈkler.ə.faɪ/",
			meaning = "làm rõ, giải thích rõ hơn",
			example = "Could you clarify this sentence for me?",
			topic = "Communication"
		),
		Flashcard(
			id = "vocab-retain",
			term = "retain",
			pronunciation = "/rɪˈteɪn/",
			meaning = "giữ lại, ghi nhớ",
			example = "Spaced repetition helps learners retain vocabulary longer.",
			topic = "Learning"
		),
		Flashcard(
			id = "vocab-consistent",
			term = "consistent",
			pronunciation = "/kənˈsɪs.tənt/",
			meaning = "đều đặn, nhất quán",
			example = "A consistent review habit improves fluency.",
			topic = "Study habit"
		),
		Flashcard(
			id = "vocab-prioritize",
			term = "prioritize",
			pronunciation = "/praɪˈɔːr.ə.taɪz/",
			meaning = "ưu tiên",
			example = "Prioritize the words that are due today.",
			topic = "Planning"
		),
		Flashcard(
			id = "vocab-recall",
			term = "recall",
			pronunciation = "/rɪˈkɔːl/",
			meaning = "nhớ lại",
			example = "Try to recall the meaning before flipping the card.",
			topic = "Memory"
		)
	)

	fun initialSchedules(todayEpochDay: Long = todayEpochDay()): Map<String, ReviewSchedule> =
		cards.associate { card ->
			card.id to ReviewSchedule(
				cardId = card.id,
				dueEpochDay = todayEpochDay
			)
		}
}
