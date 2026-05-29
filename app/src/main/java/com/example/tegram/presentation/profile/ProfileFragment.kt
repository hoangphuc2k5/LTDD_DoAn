package com.example.tegram.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tegram.domain.model.UserProfile

@Composable
fun ProfileScreen(
	user: UserProfile?,
	onBack: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(Color(0xFF081120), Color(0xFF123D66), Color(0xFF2B8CC4))
				)
			)
			.verticalScroll(rememberScrollState())
			.padding(20.dp),
		verticalArrangement = Arrangement.Top
	) {
		Spacer(modifier = Modifier.height(24.dp))
		Text(
			text = "Hồ sơ cá nhân",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
			color = Color.White
		)
		Spacer(modifier = Modifier.height(16.dp))

		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(24.dp),
			colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
		) {
			Column(modifier = Modifier.padding(20.dp)) {
				Text("UID: ${user?.uid ?: "N/A"}")
				Text("Họ tên: ${user?.fullName ?: "N/A"}")
				Text("Email: ${user?.email ?: "N/A"}")
				Text("Provider: ${user?.provider ?: "N/A"}")
				Text("Google user: ${user?.isGoogleUser ?: false}")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(24.dp),
			colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f))
		) {
			Column(modifier = Modifier.padding(20.dp)) {
				Text("Trạng thái lưu trữ", color = Color.White, fontWeight = FontWeight.Bold)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "User đã được lưu local bằng Room + DataStore, đồng thời có hook sync sang server MongoDB.",
					color = Color(0xFFDCE9F5)
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
			Text("Quay lại")
		}

		Spacer(modifier = Modifier.height(10.dp))

		Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
			Text("Đăng xuất")
		}
	}
}
