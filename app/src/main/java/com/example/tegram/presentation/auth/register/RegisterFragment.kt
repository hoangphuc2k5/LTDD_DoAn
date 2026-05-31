package com.example.tegram.presentation.auth.register

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tegram.R
import kotlinx.coroutines.launch

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.tegram.presentation.common.components.TegramBackground
import com.example.tegram.presentation.common.components.TegramButton
import com.example.tegram.presentation.common.components.TegramCard
import com.example.tegram.presentation.common.components.TegramTextField

@Composable
fun RegisterScreen(
	onRegister: suspend (String, String, String) -> Unit,
	onNavigateLogin: () -> Unit,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	var fullName by rememberSaveable { mutableStateOf("") }
	var email by rememberSaveable { mutableStateOf("") }
	var password by rememberSaveable { mutableStateOf("") }
	var confirmPassword by rememberSaveable { mutableStateOf("") }
	var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

	TegramBackground(modifier = modifier) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			androidx.compose.foundation.Image(
				painter = painterResource(id = R.drawable.z7878269644177_8b565971c843444c8c970c8f12d4ade9),
				contentDescription = "Tegram logo",
				modifier = Modifier.size(130.dp)
			)

			Spacer(modifier = Modifier.height(12.dp))

			Text(
				text = "Tạo tài khoản",
				style = MaterialTheme.typography.headlineLarge,
				fontWeight = FontWeight.ExtraBold,
				color = Color.White
			)
			Text(
				text = "Lưu dữ liệu local và đồng bộ server MongoDB",
				style = MaterialTheme.typography.bodyMedium,
				color = Color(0xFFDCE9F5)
			)

			Spacer(modifier = Modifier.height(24.dp))

			TegramCard(isDark = false) {
				Text(
					text = "Đăng ký tài khoản",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF0F172A)
				)

				Spacer(modifier = Modifier.height(24.dp))

				TegramTextField(
					value = fullName,
					onValueChange = { fullName = it },
					label = "Họ và tên",
					onDarkBackground = false
				)

				Spacer(modifier = Modifier.height(16.dp))

				TegramTextField(
					value = email,
					onValueChange = { email = it },
					label = "Email",
					onDarkBackground = false
				)

				Spacer(modifier = Modifier.height(16.dp))

				TegramTextField(
					value = password,
					onValueChange = { password = it },
					label = "Mật khẩu",
					onDarkBackground = false,
					visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
				)

				Spacer(modifier = Modifier.height(16.dp))

				TegramTextField(
					value = confirmPassword,
					onValueChange = { confirmPassword = it },
					label = "Xác nhận mật khẩu",
					onDarkBackground = false,
					visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
					trailingIcon = {
						IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
							Icon(
								imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
								contentDescription = if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
								tint = Color.Gray
							)
						}
					}
				)

				Spacer(modifier = Modifier.height(24.dp))

				TegramButton(
					text = "Tạo tài khoản",
					onClick = {
						if (password != confirmPassword) {
							context.toast("Mật khẩu xác nhận không khớp")
							return@TegramButton
						}
						scope.launch {
							runCatching { onRegister(fullName, email, password) }
								.onSuccess {
									context.toast("Đăng ký thành công")
									onNavigateLogin()
								}
								.onFailure { context.toast(it.message ?: "Đăng ký thất bại") }
						}
					}
				)

				Spacer(modifier = Modifier.height(16.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Đã có tài khoản?", color = Color(0xFF475569))
					Spacer(modifier = Modifier.width(4.dp))
					TextButton(onClick = onNavigateLogin) {
						Text("Quay lại đăng nhập")
					}
				}
			}
		}
	}
}

private fun Context.toast(message: String) {
	android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
}
