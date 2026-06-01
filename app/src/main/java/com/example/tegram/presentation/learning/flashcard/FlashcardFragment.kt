package com.example.tegram.presentation.learning.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun FlashcardScreen(
	cards: List<Flashcard>,
	isLoading: Boolean,
	errorMessage: String?,
	onRefresh: () -> Unit,
	onSeed: () -> Unit,
	onCreateFlashcard: (String, String, String, String, String) -> Unit,
	onDeleteFlashcard: (String) -> Unit,
	onBack: () -> Unit,
	onStartReview: () -> Unit,
	modifier: Modifier = Modifier
) {
	var currentIndex by remember { mutableIntStateOf(0) }
	var showAnswer by remember { mutableStateOf(false) }
	var showCreateDialog by remember { mutableStateOf(false) }
	val currentCard = cards.getOrNull(currentIndex)
	val progress = if (cards.isEmpty()) 0f else (currentIndex + 1).toFloat() / cards.size

	if (showCreateDialog) {
		CreateFlashcardDialog(
			onDismiss = { showCreateDialog = false },
			onCreate = { term, pronunciation, meaning, example, topic ->
				onCreateFlashcard(term, pronunciation, meaning, example, topic)
				showCreateDialog = false
			}
		)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					listOf(Color(0xFF08111F), Color(0xFF163B56), Color(0xFFE9F5F2))
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
					text = "Flashcard",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color.White
				)
				Text(
					text = "Chạm vào thẻ để lật mặt",
					color = Color(0xFFD8E7EF)
				)
			}
		}

		Spacer(modifier = Modifier.height(20.dp))

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

		if (currentCard == null) {
			EmptyFlashcardState(onSeed = onSeed, onCreate = { showCreateDialog = true })
			return@Column
		}

		LinearProgressIndicator(
			progress = { progress },
			modifier = Modifier.fillMaxWidth(),
			color = Color(0xFF16A085),
			trackColor = Color.White.copy(alpha = 0.25f)
		)
		Spacer(modifier = Modifier.height(10.dp))
		Text(
			text = "Thẻ ${currentIndex + 1}/${cards.size}",
			color = Color.White,
			fontWeight = FontWeight.SemiBold
		)

		Spacer(modifier = Modifier.height(18.dp))

		Card(
			modifier = Modifier
				.fillMaxWidth()
				.height(320.dp)
				.clickable { showAnswer = !showAnswer },
			shape = RoundedCornerShape(24.dp),
			colors = CardDefaults.cardColors(containerColor = Color.White),
			elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				contentAlignment = Alignment.Center
			) {
				if (showAnswer) {
					FlashcardBack(currentCard)
				} else {
					FlashcardFront(currentCard)
				}
			}
		}

		Spacer(modifier = Modifier.height(18.dp))

		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			OutlinedButton(
				onClick = {
					currentIndex = (currentIndex - 1).coerceAtLeast(0)
					showAnswer = false
				},
				modifier = Modifier.weight(1f),
				enabled = currentIndex > 0
			) {
				Text("Trước")
			}
			Button(
				onClick = {
					currentIndex = (currentIndex + 1).coerceAtMost(cards.lastIndex)
					showAnswer = false
				},
				modifier = Modifier.weight(1f),
				enabled = currentIndex < cards.lastIndex
			) {
				Text("Tiếp")
			}
		}

		Spacer(modifier = Modifier.height(12.dp))

		OutlinedButton(onClick = { showCreateDialog = true }, modifier = Modifier.fillMaxWidth()) {
			Text("Thêm flashcard")
		}

		Spacer(modifier = Modifier.height(10.dp))

		OutlinedButton(
			onClick = {
				onDeleteFlashcard(currentCard.id)
				currentIndex = currentIndex.coerceAtMost((cards.size - 2).coerceAtLeast(0))
				showAnswer = false
			},
			modifier = Modifier.fillMaxWidth()
		) {
			Text("Xóa thẻ hiện tại")
		}

		Spacer(modifier = Modifier.height(10.dp))

		Button(onClick = onStartReview, modifier = Modifier.fillMaxWidth()) {
			Text("Bắt đầu ôn tập SRS")
		}
	}
}

@Composable
private fun FlashcardFront(card: Flashcard) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			text = card.topic,
			color = Color(0xFF16A085),
			fontWeight = FontWeight.SemiBold
		)
		Spacer(modifier = Modifier.height(18.dp))
		Text(
			text = card.term,
			style = MaterialTheme.typography.displaySmall,
			fontWeight = FontWeight.Bold,
			color = Color(0xFF0F172A),
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(10.dp))
		Text(
			text = card.pronunciation,
			style = MaterialTheme.typography.titleMedium,
			color = Color(0xFF475569)
		)
	}
}

@Composable
private fun FlashcardBack(card: Flashcard) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			text = card.meaning,
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = Color(0xFF0F172A),
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(18.dp))
		Text(
			text = card.example,
			style = MaterialTheme.typography.bodyLarge,
			color = Color(0xFF334155),
			textAlign = TextAlign.Center
		)
	}
}

@Composable
private fun EmptyFlashcardState(onSeed: () -> Unit, onCreate: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White)
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Text("Chưa có flashcard để học.", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
			Spacer(modifier = Modifier.height(12.dp))
			Button(onClick = onSeed, modifier = Modifier.fillMaxWidth()) {
				Text("Tạo dữ liệu mẫu")
			}
			Spacer(modifier = Modifier.height(10.dp))
			OutlinedButton(onClick = onCreate, modifier = Modifier.fillMaxWidth()) {
				Text("Thêm flashcard")
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

@Composable
private fun CreateFlashcardDialog(
	onDismiss: () -> Unit,
	onCreate: (String, String, String, String, String) -> Unit
) {
	var term by remember { mutableStateOf("") }
	var pronunciation by remember { mutableStateOf("") }
	var meaning by remember { mutableStateOf("") }
	var example by remember { mutableStateOf("") }
	var topic by remember { mutableStateOf("") }
	val canCreate = term.isNotBlank() && meaning.isNotBlank()

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Thêm flashcard") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				OutlinedTextField(value = term, onValueChange = { term = it }, label = { Text("Từ vựng") })
				OutlinedTextField(value = meaning, onValueChange = { meaning = it }, label = { Text("Nghĩa") })
				OutlinedTextField(value = pronunciation, onValueChange = { pronunciation = it }, label = { Text("Phát âm") })
				OutlinedTextField(value = example, onValueChange = { example = it }, label = { Text("Ví dụ") })
				OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Chủ đề") })
			}
		},
		confirmButton = {
			TextButton(
				onClick = { onCreate(term, pronunciation, meaning, example, topic) },
				enabled = canCreate
			) {
				Text("Lưu")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Hủy")
			}
		}
	)
}
