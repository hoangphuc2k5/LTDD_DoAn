package com.example.tegram.presentation.learning

import com.example.tegram.domain.model.learning.epochDayToMillis
import com.example.tegram.domain.model.learning.todayEpochDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDueDate(epochDay: Long, todayEpochDay: Long = todayEpochDay()): String =
	when (epochDay) {
		todayEpochDay -> "Hôm nay"
		todayEpochDay + 1 -> "Ngày mai"
		else -> SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
			.format(Date(epochDayToMillis(epochDay)))
	}
