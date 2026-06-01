package com.example.tegram.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tegram.R
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.presentation.common.components.TegramBackground
import com.example.tegram.presentation.common.components.TegramButton
import com.example.tegram.presentation.common.components.TegramCard

@Composable
fun HomeScreen(
	user: UserProfile?,
	dailyPlan: DailyPlan,
	onOpenProfile: () -> Unit,
	onOpenFlashcards: () -> Unit,
	onOpenReview: () -> Unit,
	onOpenDailyPlan: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	TegramBackground(modifier = modifier) {
		Spacer(modifier = Modifier.height(16.dp))
		Row(verticalAlignment = Alignment.CenterVertically) {
			Image(
				painter = painterResource(id = R.drawable.z7878269644177_8b565971c843444c8c970c8f12d4ade9),
				contentDescription = "Tegram logo",
				modifier = Modifier.size(72.dp)
			)
			Spacer(modifier = Modifier.width(12.dp))
			Column {
				Text("Chào mừng trở lại", color = Color(0xFFDCE9F5))
				Text(
					text = user?.fullName ?: "Người dùng Tegram",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color.White
				)
			}
		}

		Spacer(modifier = Modifier.height(20.dp))

		TegramCard(isDark = false) {
			Text("Kế hoạch học hôm nay", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(12.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				PlanMetric("Đến hạn", dailyPlan.dueCards.toString(), Modifier.weight(1f))
				PlanMetric("Thẻ mới", dailyPlan.newCards.toString(), Modifier.weight(1f))
			}
			Spacer(modifier = Modifier.height(10.dp))
			Text(
				text = "Dự kiến ${dailyPlan.estimatedMinutes} phút học tập.",
				color = Color(0xFF475569)
			)
			Spacer(modifier = Modifier.height(14.dp))
			TegramButton(
				text = "Ôn tập SRS",
				onClick = onOpenReview,
				enabled = dailyPlan.dueCards > 0
			)
			Spacer(modifier = Modifier.height(10.dp))
			TegramButton(
				text = "Xem Daily Plan",
				onClick = onOpenDailyPlan,
				isOutlined = true
			)
		}

		Spacer(modifier = Modifier.height(16.dp))

		TegramCard(isDark = true) {
			Text("Flashcard & SRS", color = Color.White, fontWeight = FontWeight.Bold)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Lật thẻ để tự kiểm tra trước, sau đó dùng SM-2 để lên lịch ôn tiếp theo.",
				color = Color(0xFFDCE9F5)
			)
			Spacer(modifier = Modifier.height(14.dp))
			TegramButton(text = "Học Flashcard", onClick = onOpenFlashcards)
		}

		Spacer(modifier = Modifier.height(16.dp))

		TegramCard(isDark = false) {
			Text("Thông tin tài khoản", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
			Spacer(modifier = Modifier.height(10.dp))
			Text("Email: ${user?.email ?: "Chưa xác định"}")
			Text("Nguồn đăng nhập: ${user?.provider ?: "Local"}")
			Text("Đồng bộ lúc: ${user?.syncedAt ?: 0L}")
		}

		Spacer(modifier = Modifier.height(16.dp))

		TegramButton(text = "Xem hồ sơ", onClick = onOpenProfile)

		Spacer(modifier = Modifier.height(10.dp))

		TegramButton(text = "Đăng xuất", onClick = onLogout, isOutlined = true)
	}
}

@Composable
private fun PlanMetric(label: String, value: String, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.background(Color(0xFFEAF3EF), RoundedCornerShape(16.dp))
			.padding(14.dp)
	) {
		Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Text(label, color = Color(0xFF475569))
	}
}
