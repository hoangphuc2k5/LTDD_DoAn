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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tegram.domain.model.UserProfile
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenStatistics: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B1528), Color(0xFF142B50), Color(0xFF1E3F75))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- Header Section ---
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
                val firstLetter = user?.fullName?.take(1)?.uppercase(Locale.getDefault()) ?: "T"
                Text(
                    text = firstLetter,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2B8CC4),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
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
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Welcome Message & Instructions ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Tegram English Learning Hub 🚀",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hãy chọn các chức năng học tập dưới đây để tiến hành ôn luyện từ vựng và theo dõi sự tiến bộ của bản thân.",
                    color = Color(0xFFDCE9F5),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Function Cards Title ---
        Text(
            text = "DANH MỤC CHỨC NĂNG",
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Functions Grid (2 columns) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Function 2: Quản lý từ vựng
                FunctionHubCard(
                    title = "Quản lý Từ vựng",
                    subtitle = "Danh sách bộ từ, thêm/sửa từ vựng",
                    emoji = "📚",
                    onClick = { /* Placeholder */ },
                    modifier = Modifier.weight(1f),
                    isEnabled = false
                )

                // Function 3: Học Flashcard & SRS
                FunctionHubCard(
                    title = "Học Flashcard & SRS",
                    subtitle = "Ôn tập thẻ, thuật toán SM-2",
                    emoji = "🎴",
                    onClick = { /* Placeholder */ },
                    modifier = Modifier.weight(1f),
                    isEnabled = false
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Function 4: Thống kê & Tiến độ
                FunctionHubCard(
                    title = "Thống kê & Tiến độ",
                    subtitle = "Streak, biểu đồ ôn tập, trình độ",
                    emoji = "📊",
                    onClick = onOpenStatistics,
                    modifier = Modifier.weight(1f),
                    isEnabled = true,
                    highlightColor = Color(0xFF2B8CC4)
                )

                // Function 5: Hồ sơ & Cài đặt
                FunctionHubCard(
                    title = "Hồ sơ & Cài đặt",
                    subtitle = "Thông tin cá nhân, nhắc học",
                    emoji = "⚙️",
                    onClick = onOpenProfile,
                    modifier = Modifier.weight(1f),
                    isEnabled = true
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Logout Action Button ---
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.85f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Đăng xuất", color = Color.White)
        }
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
                Text(
                    text = emoji,
                    fontSize = 28.sp
                )
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
