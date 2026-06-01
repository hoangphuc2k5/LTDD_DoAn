package com.example.tegram.presentation.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tegram.R
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.presentation.common.components.TegramBackground
import com.example.tegram.presentation.common.components.TegramButton
import com.example.tegram.presentation.common.components.TegramCard
import java.util.Locale

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
		TegramBackgroundContent(
			user = user,
			dailyPlan = dailyPlan,
			onOpenProfile = onOpenProfile,
			onOpenFlashcards = onOpenFlashcards,
			onOpenReview = onOpenReview,
			onOpenDailyPlan = onOpenDailyPlan,
			onLogout = onLogout
		)
	}
}

@Composable
private fun TegramBackgroundContent(
	user: UserProfile?,
	dailyPlan: DailyPlan,
	onOpenProfile: () -> Unit,
	onOpenFlashcards: () -> Unit,
	onOpenReview: () -> Unit,
	onOpenDailyPlan: () -> Unit,
	onLogout: () -> Unit
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(56.dp)
					.clip(CircleShape)
					.background(Color(0xFF2B8CC4).copy(alpha = 0.2f)),
				contentAlignment = Alignment.Center
			) {
				val firstLetter = remember(user?.fullName) {
					user?.fullName?.trim()?.take(1)?.uppercase(Locale.getDefault()) ?: "T"
				}
				Text(
					text = firstLetter,
					style = MaterialTheme.typography.titleLarge,
					color = Color(0xFF2B8CC4),
					fontWeight = FontWeight.Bold
				)
			}
			Spacer(modifier = Modifier.width(16.dp))
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "Chào mừng trở lại,",
					color = Color(0xFFDCE9F5),
					fontSize = 14.sp
				)
				Text(
					text = user?.fullName ?: "Người dùng Tegram",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = Color.White
				)
			}
			Icon(
				imageVector = Icons.Default.Star,
				contentDescription = null,
				tint = Color(0xFFFFD166)
			)
		}

			Spacer(modifier = Modifier.height(24.dp))

			TegramCard(isDark = false) {
				Text(
					text = "Kế hoạch học hôm nay",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF0F172A)
				)
				Spacer(modifier = Modifier.height(12.dp))
				Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
					PlanMetric("Tổng thẻ", dailyPlan.totalCards.toString(), Modifier.weight(1f))
					PlanMetric("Đến hạn", dailyPlan.dueCards.toString(), Modifier.weight(1f))
				}
				Spacer(modifier = Modifier.height(10.dp))
				Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
					PlanMetric("Thẻ mới", dailyPlan.newCards.toString(), Modifier.weight(1f))
					PlanMetric("Quá hạn", dailyPlan.overdueCards.toString(), Modifier.weight(1f))
				}
				Spacer(modifier = Modifier.height(12.dp))
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
					text = "Mở Daily Plan",
					onClick = onOpenDailyPlan,
					isOutlined = true
				)
			}

			Spacer(modifier = Modifier.height(16.dp))

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onOpenProfile() },
				shape = RoundedCornerShape(24.dp),
				colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f))
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(
						text = "Thông tin tài khoản",
						color = Color.White,
						fontWeight = FontWeight.Bold,
						style = MaterialTheme.typography.titleMedium
					)
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						text = "Email: ${user?.email ?: "Chưa xác định"}",
						color = Color(0xFFDCE9F5)
					)
					Text(
						text = "Nguồn đăng nhập: ${user?.provider ?: "Local"}",
						color = Color(0xFFDCE9F5)
					)
					Text(
						text = "Đồng bộ lúc: ${user?.syncedAt ?: 0L}",
						color = Color(0xFFDCE9F5)
					)
					Spacer(modifier = Modifier.height(12.dp))
					Button(
						onClick = onOpenProfile,
						modifier = Modifier.fillMaxWidth(),
						shape = RoundedCornerShape(12.dp),
						colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B8CC4))
					) {
						Icon(Icons.Default.Person, contentDescription = null)
						Spacer(modifier = Modifier.width(8.dp))
						Text("Xem hồ sơ")
					}
				}
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

			TegramButton(
				text = "Đăng xuất",
				onClick = onLogout,
				isOutlined = true
			)
		}
	}

@Composable
fun HomeScreen(
	user: UserProfile?,
	onOpenProfile: () -> Unit,
	onOpenVocabulary: () -> Unit,
	onOpenStatistics: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	TegramBackground(modifier = modifier) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Box(
					modifier = Modifier
						.size(56.dp)
						.clip(CircleShape)
						.background(Color(0xFF2B8CC4).copy(alpha = 0.2f)),
					contentAlignment = Alignment.Center
				) {
					val firstLetter = remember(user?.fullName) {
						user?.fullName?.trim()?.take(1)?.uppercase(Locale.getDefault()) ?: "T"
					}
					Text(
						text = firstLetter,
						style = MaterialTheme.typography.titleLarge,
						color = Color(0xFF2B8CC4),
						fontWeight = FontWeight.Bold
					)
				}
				Spacer(modifier = Modifier.width(16.dp))
				Column(modifier = Modifier.weight(1f)) {
					Text("Chào mừng trở lại", color = Color(0xFFDCE9F5))
					Text(
						text = user?.fullName ?: "Người dùng Tegram",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = Color.White
					)
				}
			}

			Spacer(modifier = Modifier.height(24.dp))

			TegramCard(isDark = false) {
				Text(
					text = "DANH MỤC CHỨC NĂNG",
					color = Color(0xFF0F172A),
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.height(14.dp))
				FunctionHubCard(
					title = "Quản lý Từ vựng",
					subtitle = "Danh sách bộ từ, thêm/sửa từ vựng",
					emoji = "📚",
					onClick = onOpenVocabulary,
					isEnabled = true,
					highlightColor = Color(0xFF2B8CC4)
				)
				Spacer(modifier = Modifier.height(12.dp))
				FunctionHubCard(
					title = "Thống kê & Tiến độ",
					subtitle = "Streak, biểu đồ ôn tập, trình độ",
					emoji = "📊",
					onClick = onOpenStatistics,
					isEnabled = true
				)
				Spacer(modifier = Modifier.height(12.dp))
				FunctionHubCard(
					title = "Hồ sơ & Cài đặt",
					subtitle = "Thông tin cá nhân, nhắc học",
					emoji = "⚙️",
					onClick = onOpenProfile,
					isEnabled = true
				)
			}

			Spacer(modifier = Modifier.height(16.dp))
			TegramButton(text = "Đăng xuất", onClick = onLogout, isOutlined = true)
		}
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

@Composable
fun FunctionHubCard(
	title: String,
	subtitle: String,
	emoji: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	isEnabled: Boolean = true,
	highlightColor: Color? = null
) {
	Card(
		modifier = modifier
			.height(130.dp)
			.clickable(enabled = isEnabled) { onClick() },
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(
			containerColor = when {
				!isEnabled -> Color.White.copy(alpha = 0.02f)
				highlightColor != null -> highlightColor.copy(alpha = 0.15f)
				else -> Color.White.copy(alpha = 0.06f)
			}
		),
		border = if (highlightColor != null && isEnabled) {
			androidx.compose.foundation.BorderStroke(1.dp, highlightColor.copy(alpha = 0.4f))
		} else null
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(14.dp),
			verticalArrangement = Arrangement.SpaceBetween,
			horizontalAlignment = Alignment.Start
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(text = emoji, fontSize = 28.sp)
				if (!isEnabled) {
					Box(
						modifier = Modifier
							.background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
							.padding(horizontal = 6.dp, vertical = 2.dp)
					) {
						Text(
							text = "Khóa",
							color = Color.White.copy(alpha = 0.4f),
							fontSize = 9.sp,
							fontWeight = FontWeight.Bold
						)
					}
				}
			}

			Column {
				Text(
					text = title,
					color = if (isEnabled) Color.White else Color.White.copy(alpha = 0.35f),
					fontWeight = FontWeight.Bold,
					fontSize = 14.sp
				)
				Spacer(modifier = Modifier.height(2.dp))
				Text(
					text = subtitle,
					color = if (isEnabled) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.2f),
					fontSize = 10.sp,
					lineHeight = 12.sp
				)
			}
		}
	}
}
