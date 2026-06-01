package com.example.tegram.presentation.learning.dailyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay
import com.example.tegram.presentation.learning.formatDueDate

@Composable
fun DailyPlanScreen(
	plan: DailyPlan,
	cards: List<Flashcard>,
	schedules: Map<String, ReviewSchedule>,
	isLoading: Boolean,
	errorMessage: String?,
	onRefresh: () -> Unit,
	onSeed: () -> Unit,
	onBack: () -> Unit,
	onStartFlashcards: () -> Unit,
	onStartReview: () -> Unit,
	modifier: Modifier = Modifier
) {
	val today = remember { todayEpochDay() }
	val dueCards = remember(cards, schedules) {
		cards.filter { schedules[it.id]?.isDue(today) == true }
	}
	val upcomingCards = remember(cards, schedules) {
		cards
			.filter { (schedules[it.id]?.dueEpochDay ?: today) > today }
			.sortedBy { schedules[it.id]?.dueEpochDay }
			.take(3)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					listOf(Color(0xFF07111F), Color(0xFF17445C), Color(0xFFF3F7EF))
				)
			)
			.verticalScroll(rememberScrollState())
			.padding(20.dp)
	) {
		Spacer(modifier = Modifier.height(20.dp))
		Row(verticalAlignment = Alignment.CenterVertically) {
			OutlinedButton(onClick = onBack) {
				Text("Trở lại")
			}
			Spacer(modifier = Modifier.width(12.dp))
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "Daily Plan",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color.White
				)
				Text("Kế hoạch học hôm nay", color = Color(0xFFD8E7EF))
			}
		}

		Spacer(modifier = Modifier.height(18.dp))
		errorMessage?.let {
			ErrorCard(message = it, onRetry = onRefresh)
			Spacer(modifier = Modifier.height(14.dp))
		}
		if (isLoading) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				CircularProgressIndicator(color = Color.White)
				Spacer(modifier = Modifier.width(10.dp))
				Text("Đang đồng bộ...", color = Color.White)
			}
			Spacer(modifier = Modifier.height(14.dp))
		}
		PlanSummary(plan)
		Spacer(modifier = Modifier.height(16.dp))

		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			Button(
				onClick = onStartReview,
				modifier = Modifier.weight(1f),
				enabled = plan.dueCards > 0
			) {
				Text("Ôn SRS")
			}
			OutlinedButton(onClick = onStartFlashcards, modifier = Modifier.weight(1f)) {
				Text("Học thẻ")
			}
		}
		Spacer(modifier = Modifier.height(10.dp))
		OutlinedButton(onClick = onSeed, modifier = Modifier.fillMaxWidth()) {
			Text("Tạo lại dữ liệu mẫu")
		}

		Spacer(modifier = Modifier.height(16.dp))
		CardList(
			title = "Đến hạn hôm nay",
			emptyText = "Không còn thẻ nào đến hạn.",
			cards = dueCards,
			schedules = schedules
		)
		Spacer(modifier = Modifier.height(16.dp))
		CardList(
			title = "Lịch sắp tới",
			emptyText = "Chưa có lịch ôn tiếp theo.",
			cards = upcomingCards,
			schedules = schedules
		)
	}
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2))
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(message, color = Color(0xFF9F1239), fontWeight = FontWeight.SemiBold)
			Spacer(modifier = Modifier.height(10.dp))
			OutlinedButton(onClick = onRetry) {
				Text("Thử lại")
			}
		}
	}
}

@Composable
private fun PlanSummary(plan: DailyPlan) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text("Tổng quan", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(14.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				MetricTile("Tổng thẻ", plan.totalCards.toString(), Modifier.weight(1f))
				MetricTile("Đến hạn", plan.dueCards.toString(), Modifier.weight(1f))
			}
			Spacer(modifier = Modifier.height(10.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				MetricTile("Thẻ mới", plan.newCards.toString(), Modifier.weight(1f))
				MetricTile("Quá hạn", plan.overdueCards.toString(), Modifier.weight(1f))
			}
			Spacer(modifier = Modifier.height(14.dp))
			Text(
				text = "Thời lượng dự kiến: ${plan.estimatedMinutes} phút",
				color = Color(0xFF334155),
				fontWeight = FontWeight.SemiBold
			)
			plan.nextDueEpochDay?.let {
				Text("Lần ôn kế tiếp: ${formatDueDate(it)}", color = Color(0xFF475569))
			}
		}
	}
}

@Composable
private fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.background(Color(0xFFEAF3EF), RoundedCornerShape(16.dp))
			.padding(14.dp)
	) {
		Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Text(label, color = Color(0xFF475569))
	}
}

@Composable
private fun CardList(
	title: String,
	emptyText: String,
	cards: List<Flashcard>,
	schedules: Map<String, ReviewSchedule>
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
	) {
		Column(modifier = Modifier.padding(18.dp)) {
			Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(10.dp))
			if (cards.isEmpty()) {
				Text(emptyText, color = Color(0xFF64748B))
			} else {
				cards.forEach { card ->
					val dueLabel = schedules[card.id]?.dueEpochDay?.let { formatDueDate(it) } ?: "Chưa có lịch"
					Column(modifier = Modifier.padding(vertical = 8.dp)) {
						Text(card.term, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
						Text("$dueLabel • ${card.meaning}", color = Color(0xFF475569))
					}
				}
			}
		}
	}
}
