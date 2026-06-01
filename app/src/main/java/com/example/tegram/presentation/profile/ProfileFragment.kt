package com.example.tegram.presentation.profile

import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.presentation.common.components.*
import com.example.tegram.service.notification.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    user: UserProfile?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedReminderTime by viewModel.reminderTime.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    
    val reminderDisplay = savedReminderTime ?: "Chưa đặt"
    
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Vui lòng cấp quyền thông báo", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProfileUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                isEditing = false
                selectedImageUri = null
                viewModel.resetUiState()
            }
            is ProfileUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    fun scheduleNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        val now = Calendar.getInstance()
        if (calendar.before(now)) calendar.add(Calendar.DAY_OF_YEAR, 1)
        val delay = calendar.timeInMillis - now.timeInMillis
        
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("daily_reminder")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        viewModel.saveReminderTime(timeString)
        Toast.makeText(context, "Đã đặt lịch nhắc học lúc $timeString", Toast.LENGTH_LONG).show()
    }

    TegramBackground(modifier = modifier) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TegramSectionTitle(text = "Hồ sơ cá nhân")
            if (!isEditing) {
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TegramCard(isDark = false) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable(enabled = isEditing) { imagePicker.launch("image/*") }
                ) {
                    AsyncImage(
                        model = selectedImageUri ?: currentUser?.photoUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Đổi ảnh", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isEditing) {
                    TegramTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = "Họ tên",
                        onDarkBackground = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TegramButton(
                            text = "Hủy",
                            onClick = { 
                                isEditing = false
                                selectedImageUri = null
                                editName = currentUser?.fullName ?: ""
                            },
                            isOutlined = true,
                            onDarkBackground = false,
                            modifier = Modifier.weight(1f)
                        )
                        TegramButton(
                            text = if (uiState is ProfileUiState.Loading) "Đang lưu..." else "Lưu",
                            onClick = { viewModel.updateProfile(editName, selectedImageUri) },
                            enabled = uiState !is ProfileUiState.Loading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Text(
                        text = currentUser?.fullName ?: "N/A",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = currentUser?.email ?: "N/A", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TegramCard(isDark = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cài đặt nhắc nhở", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Nhắc tôi học lúc: $reminderDisplay", color = Color(0xFFDCE9F5))
            Spacer(modifier = Modifier.height(12.dp))
            TegramButton(
                text = "Chọn giờ nhắc học",
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, h, m -> scheduleNotification(h, m) },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        TegramButton(text = "Đăng xuất", onClick = onLogout, isOutlined = true)
    }
}
