package com.example.tegram.presentation.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tegram.data.local.entity.DailyProgressEntity
import com.example.tegram.presentation.home.ProgressViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val progressList by viewModel.recentProgress.collectAsState()

    val totalReviews = user?.totalReviews ?: 0
    val correctReviews = user?.correctReviews ?: 0
    val retentionRate = if (totalReviews > 0) (correctReviews.toFloat() / totalReviews.toFloat()) else 0.85f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B1528), Color(0xFF142B50), Color(0xFF1E3F75))
                )
            )
            .padding(horizontal = 20.dp)
    ) {
        // --- Custom App Bar ---
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Thống Kê & Tiến Độ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Streak Flame Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Chuỗi học tập (Streak)",
                                color = Color(0xFFDCE9F5),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${user?.streak ?: 0} Ngày liên tiếp",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "🔥",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Streak Weekly View
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        val calendar = Calendar.getInstance()
                        val todayDayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> 0
                            Calendar.TUESDAY -> 1
                            Calendar.WEDNESDAY -> 2
                            Calendar.THURSDAY -> 3
                            Calendar.FRIDAY -> 4
                            Calendar.SATURDAY -> 5
                            else -> 6 // Sunday
                        }

                        daysOfWeek.forEachIndexed { index, day ->
                            val isPast = index <= todayDayOfWeek
                            val isStudied = isPast && (user?.streak ?: 0) > (todayDayOfWeek - index)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isStudied -> Color(0xFFE07A5F) // Orange-red for studied
                                                isPast -> Color(0xFF334155) // Gray-blue for missed
                                                else -> Color(0xFF1E293B) // Dark for future
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isStudied) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Studied",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = if (isPast) "✗" else "•",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    color = if (index == todayDayOfWeek) Color(0xFF2B8CC4) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (index == todayDayOfWeek) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // --- Level Progress Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val words = user?.wordsLearned ?: 0
                    val currentLevel = user?.level ?: "A1"
                    val (nextLevel, nextLevelTarget, levelDesc) = when (currentLevel) {
                        "A1" -> Triple("A2", 50, "Cơ bản (Sơ cấp)")
                        "A2" -> Triple("B1", 100, "Sơ trung cấp")
                        "B1" -> Triple("B2", 200, "Trung cấp")
                        "B2" -> Triple("C1", 500, "Trung cao cấp")
                        "C1" -> Triple("C2", 1000, "Cao cấp")
                        else -> Triple("Max", 2000, "Thông thạo hoàn toàn")
                    }

                    val progress = if (words >= nextLevelTarget) 1.0f else (words.toFloat() / nextLevelTarget.toFloat())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tiến trình Cấp độ: $currentLevel → $nextLevel",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$words / $nextLevelTarget từ",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF2B8CC4),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Đạt $nextLevelTarget từ vựng để mở khóa trình độ $nextLevel ($levelDesc)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // --- Retention Circle and Summary Grid ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Retention Gauge
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        RetentionGauge(
                            retentionRate = retentionRate,
                            totalReviews = totalReviews,
                            modifier = Modifier
                                .size(130.dp)
                                .padding(10.dp)
                        )
                    }
                }

                // Overall Stats Grid
                Card(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Tổng Lượt Ôn", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("$totalReviews lượt", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Column {
                            Text("Ôn Trả Lời Đúng", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("$correctReviews lượt", color = Color(0xFF3CD070), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Column {
                            Text("Từ vựng học mới", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("+${user?.wordsLearned ?: 0} từ", color = Color(0xFF2B8CC4), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- Weekly Activity Chart Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Hoạt Động Ôn Tập Tuần",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Số lượng thẻ từ đã ôn tập trong 7 ngày qua",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    WeeklyActivityChart(
                        progressList = progressList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }

            // --- CEFR Level Distribution Progress ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Phân Bố Cấp Độ Từ Vựng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val words = user?.wordsLearned ?: 0

                    CefrProgressRow("A1 (Sơ cấp - 50 từ)", words, 50, Color(0xFF2B8CC4))
                    Spacer(modifier = Modifier.height(12.dp))
                    CefrProgressRow("A2 (Sơ trung cấp - 100 từ)", words, 100, Color(0xFF3CD070))
                    Spacer(modifier = Modifier.height(12.dp))
                    CefrProgressRow("B1 (Trung cấp - 200 từ)", words, 200, Color(0xFFE9C46A))
                    Spacer(modifier = Modifier.height(12.dp))
                    CefrProgressRow("B2 (Trung cao cấp - 500 từ)", words, 500, Color(0xFFE07A5F))
                    Spacer(modifier = Modifier.height(12.dp))
                    CefrProgressRow("C1 & C2 (Cao cấp - 1000+ từ)", words, 1000, Color(0xFF9B5DE5))
                }
            }

            // --- Learning Achievements ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Danh Hiệu Đạt Được",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Achievement 1: Streak Badge
                    AchievementRow(
                        title = "Bền Bỉ Vô Đối",
                        desc = "Có chuỗi tự học liên tiếp trên 3 ngày",
                        isUnlocked = (user?.streak ?: 0) >= 3,
                        emoji = "⚡"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Achievement 2: Vocabulary Badge
                    AchievementRow(
                        title = "Vua Từ Vựng B1",
                        desc = "Thuộc trên 100 từ vựng và đạt mức B1",
                        isUnlocked = (user?.wordsLearned ?: 0) >= 100,
                        emoji = "🏆"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Achievement 3: Perfect Practice Badge
                    AchievementRow(
                        title = "Trí Nhớ Siêu Phàm",
                        desc = "Đạt tỉ lệ nhớ bài trên 80%",
                        isUnlocked = totalReviews >= 50 && retentionRate >= 0.8f,
                        emoji = "🧠"
                    )
                }
            }

            // --- Interactive Simulation (For Demo / Grading) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛠 KHU VỰC MÔ PHỎNG DÀNH CHO GIÁO VIÊN/TESTER",
                        color = Color(0xFFE9C46A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.simulateLearnWord(true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3CD070)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ôn Đúng (+1 từ)", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.simulateLearnWord(false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A5F)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ôn Sai (+1 ôn)", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun WeeklyActivityChart(
    progressList: List<DailyProgressEntity>,
    modifier: Modifier = Modifier
) {
    val sortedList = progressList.sortedBy { it.date }
    val displayList = if (sortedList.size >= 7) {
        sortedList.takeLast(7)
    } else {
        val list = mutableListOf<DailyProgressEntity>()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            val tempCal = cal.clone() as Calendar
            tempCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = formatter.format(tempCal.time)
            val match = sortedList.find { it.date == dateStr }
            list.add(match ?: DailyProgressEntity(dateStr, 0, 0, 0))
        }
        list
    }

    val maxVal = displayList.maxOfOrNull { it.reviewsCount }?.coerceAtLeast(10) ?: 10

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barCount = displayList.size
        val spacing = 12.dp.toPx()
        val totalSpacing = spacing * (barCount + 1)
        val barWidth = (width - totalSpacing) / barCount

        val dateLabelFormatter = SimpleDateFormat("EE", Locale.getDefault())
        val dbDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        displayList.forEachIndexed { index, progress ->
            val barHeight = (progress.reviewsCount.toFloat() / maxVal.toFloat()) * (height - 35.dp.toPx())
            val x = spacing + index * (barWidth + spacing)
            val y = height - barHeight - 20.dp.toPx()

            // Draw Bar
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF2B8CC4))
                ),
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Draw review count on top of bar
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawText(
                    progress.reviewsCount.toString(),
                    x + barWidth / 2,
                    y - 6.dp.toPx(),
                    paint
                )
            }

            // Draw day labels
            val dayLabel = runCatching {
                val date = dbDateFormatter.parse(progress.date)
                dateLabelFormatter.format(date ?: Date())
            }.getOrDefault(progress.date.substringAfterLast("-"))

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(180, 220, 233, 245)
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    dayLabel,
                    x + barWidth / 2,
                    height - 4.dp.toPx(),
                    paint
                )
            }
        }
    }
}

@Composable
fun RetentionGauge(
    retentionRate: Float,
    totalReviews: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circleSize = size
            val radius = circleSize.minDimension / 2 - 6.dp.toPx()
            val arcCenter = center

            // Background circle arc
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = radius,
                center = arcCenter,
                style = Stroke(width = 10.dp.toPx())
            )

            // Retention arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF3CD070), Color(0xFFE9C46A), Color(0xFF3CD070))
                ),
                startAngle = -90f,
                sweepAngle = retentionRate * 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(arcCenter.x - radius, arcCenter.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(retentionRate * 100).toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Tỷ lệ nhớ",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun CefrProgressRow(
    levelLabel: String,
    currentWords: Int,
    targetWords: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(levelLabel, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${currentWords.coerceAtMost(targetWords)} / $targetWords",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        val progress = (currentWords.toFloat() / targetWords.toFloat()).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun AchievementRow(
    title: String,
    desc: String,
    isUnlocked: Boolean,
    emoji: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isUnlocked) Color(0xFF2B8CC4).copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) Color(0xFFE9C46A).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isUnlocked) emoji else "🔒",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = desc,
                color = if (isUnlocked) Color(0xFFDCE9F5) else Color.White.copy(alpha = 0.25f),
                fontSize = 11.sp
            )
        }

        if (isUnlocked) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Unlocked",
                tint = Color(0xFF3CD070),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
