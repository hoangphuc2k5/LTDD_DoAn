package com.example.tegram.presentation.learning.review

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tegram.domain.model.learning.Flashcard
import com.example.tegram.domain.model.learning.ReviewRating
import com.example.tegram.domain.model.learning.ReviewSchedule
import com.example.tegram.domain.model.learning.todayEpochDay

@Composable
fun SrsReviewScreen(
	cards: List<Flashcard>,
	schedules: Map<String, ReviewSchedule>,
	isLoading: Boolean,
	errorMessage: String?,
	onRefresh: () -> Unit,
	onRate: (String, ReviewRating) -> Unit,
	onBack: () -> Unit,
	onOpenDailyPlan: () -> Unit,
	modifier: Modifier = Modifier
) {
	val today = remember { todayEpochDay() }
	val initialDueIds = remember(cards, schedules) {
		cards
			.filter { schedules[it.id]?.isDue(today) == true }
			.map { it.id }
	}
	val queue = remember(initialDueIds) { mutableStateListOf<String>().also { it.addAll(initialDueIds) } }
	var reviewedCount by remember(initialDueIds) { mutableIntStateOf(0) }
	var repeatedAgainIds by remember(initialDueIds) { mutableStateOf(emptySet<String>()) }
	var showAnswer by remember(initialDueIds) { mutableStateOf(false) }
	val currentCard = queue.firstOrNull()?.let { id -> cards.firstOrNull { it.id == id } }
	val totalSessionCards = initialDueIds.size
	val progress = if (totalSessionCards == 0) 1f else reviewedCount.toFloat() / totalSessionCards

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					listOf(Color(0xFF0B1020), Color(0xFF17445C), Color(0xFFF4F7F2))
				)
			)
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
					text = "Ôn tập SRS",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color.White
				)
				Text("Again / Hard / Good / Easy", color = Color(0xFFD8E7EF))
			}
		}

		Spacer(modifier = Modifier.height(18.dp))
		errorMessage?.let {
			ErrorCard(message = it, onRetry = onRefresh)
			Spacer(modifier = Modifier.height(12.dp))
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
			Spacer(modifier = Modifier.height(12.dp))
		}
		LinearProgressIndicator(
			progress = { progress.coerceIn(0f, 1f) },
			modifier = Modifier.fillMaxWidth(),
			color = Color(0xFFF59E0B),
			trackColor = Color.White.copy(alpha = 0.25f)
		)
		Spacer(modifier = Modifier.height(14.dp))

		when {
			totalSessionCards == 0 -> EmptyReviewState(onOpenDailyPlan)
			currentCard == null -> ReviewDoneState(reviewedCount, onOpenDailyPlan)
			else -> ReviewCard(
				card = currentCard,
				showAnswer = showAnswer,
				reviewedCount = reviewedCount,
				totalSessionCards = totalSessionCards,
				onReveal = { showAnswer = true },
				onRate = { rating ->
					onRate(currentCard.id, rating)
					reviewedCount += 1
					queue.removeAt(0)
					if (rating == ReviewRating.Again && currentCard.id !in repeatedAgainIds) {
						repeatedAgainIds = repeatedAgainIds + currentCard.id
						queue.add(currentCard.id)
					}
					showAnswer = false
				}
			)
		}
	}
}

@Composable
private fun ReviewCard(
	card: Flashcard,
	showAnswer: Boolean,
	reviewedCount: Int,
	totalSessionCards: Int,
	onReveal: () -> Unit,
	onRate: (ReviewRating) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
	) {
		Column(
			modifier = Modifier.padding(22.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "Câu ${reviewedCount + 1}/$totalSessionCards",
				color = Color(0xFF64748B),
				fontWeight = FontWeight.SemiBold
			)
			Spacer(modifier = Modifier.height(20.dp))
			Text(
				text = card.term,
				style = MaterialTheme.typography.displaySmall,
				fontWeight = FontWeight.Bold,
				color = Color(0xFF0F172A),
				textAlign = TextAlign.Center
			)
			Text(card.pronunciation, color = Color(0xFF64748B))
			Spacer(modifier = Modifier.height(24.dp))

			if (showAnswer) {
				Text(
					text = card.meaning,
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF0F172A),
					textAlign = TextAlign.Center
				)
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					text = card.example,
					color = Color(0xFF334155),
					textAlign = TextAlign.Center
				)
				Spacer(modifier = Modifier.height(22.dp))
				RatingButtons(onRate = onRate)
			} else {
				Button(onClick = onReveal, modifier = Modifier.fillMaxWidth()) {
					Text("Hiện đáp án")
				}
			}
		}
	}
}

@Composable
private fun RatingButtons(onRate: (ReviewRating) -> Unit) {
	val colors = mapOf(
		ReviewRating.Again to Color(0xFFDC2626),
		ReviewRating.Hard to Color(0xFFF59E0B),
		ReviewRating.Good to Color(0xFF16A34A),
		ReviewRating.Easy to Color(0xFF2563EB)
	)

	Column(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		ReviewRating.entries.forEach { rating ->
			Button(
				onClick = { onRate(rating) },
				modifier = Modifier.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(containerColor = colors.getValue(rating))
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text(rating.label, fontWeight = FontWeight.Bold)
					Text(rating.helper, style = MaterialTheme.typography.bodySmall)
				}
			}
		}
	}
}

@Composable
private fun EmptyReviewState(onOpenDailyPlan: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White)
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text("Hôm nay chưa có thẻ đến hạn.", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(8.dp))
			Text("Bạn có thể xem Daily Plan để biết lịch học tiếp theo.", color = Color(0xFF475569))
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = onOpenDailyPlan, modifier = Modifier.fillMaxWidth()) {
				Text("Mở Daily Plan")
			}
		}
	}
}

@Composable
private fun ReviewDoneState(
	reviewedCount: Int,
	onOpenDailyPlan: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White)
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text("Hoàn tất phiên ôn tập", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(8.dp))
			Text("Đã xử lý $reviewedCount lượt trả lời.", color = Color(0xFF475569))
			Spacer(modifier = Modifier.height(16.dp))
			Button(onClick = onOpenDailyPlan, modifier = Modifier.fillMaxWidth()) {
				Text("Xem kế hoạch học")
			}
		}
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
