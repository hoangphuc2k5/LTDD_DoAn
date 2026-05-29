package com.example.tegram.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tegram.R
import com.example.tegram.domain.model.UserProfile

@Composable
fun HomeScreen(
	user: UserProfile?,
	onOpenProfile: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(Color(0xFF07111F), Color(0xFF123D66), Color(0xFF2B8CC4))
				)
			)
			.verticalScroll(rememberScrollState())
			.padding(20.dp),
		verticalArrangement = Arrangement.Top
	) {
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

		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(24.dp),
			colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
		) {
			Column(modifier = Modifier.padding(20.dp)) {
				Text("Thông tin tài khoản", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
				Spacer(modifier = Modifier.height(10.dp))
				Text("Email: ${user?.email ?: "Chưa xác định"}")
				Text("Nguồn đăng nhập: ${user?.provider ?: "Local"}")
				Text("Đồng bộ lúc: ${user?.syncedAt ?: 0L}")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(24.dp),
			colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f))
		) {
			Column(modifier = Modifier.padding(20.dp)) {
				Text("Nền tảng học từ vựng", color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "Sẵn sàng cho việc luyện tập, đồng bộ dữ liệu và mở rộng sau này với MongoDB.",
					color = Color(0xFFDCE9F5)
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Button(onClick = onOpenProfile, modifier = Modifier.fillMaxWidth()) {
			Text("Xem hồ sơ")
		}

		Spacer(modifier = Modifier.height(10.dp))

		Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
			Text("Đăng xuất")
		}
	}
}
