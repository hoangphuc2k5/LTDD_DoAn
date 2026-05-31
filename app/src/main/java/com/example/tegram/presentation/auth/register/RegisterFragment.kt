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

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						Color(0xFF081120),
						Color(0xFF123D66),
						Color(0xFF2B8CC4)
					)
				)
			)
			.padding(20.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			androidx.compose.foundation.Image(
				painter = painterResource(id = R.drawable.z7878269644177_8b565971c843444c8c970c8f12d4ade9),
				contentDescription = "Tegram logo",
				modifier = Modifier.size(140.dp)
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

			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(28.dp),
				colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f)),
				elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Text(
						text = "Đăng ký tài khoản",
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
						color = Color(0xFF0F172A)
					)

					Spacer(modifier = Modifier.height(16.dp))

					OutlinedTextField(
						value = fullName,
						onValueChange = { fullName = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Họ và tên") },
						singleLine = true
					)

					Spacer(modifier = Modifier.height(12.dp))

					OutlinedTextField(
						value = email,
						onValueChange = { email = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Email") },
						singleLine = true
					)

					Spacer(modifier = Modifier.height(12.dp))

					OutlinedTextField(
						value = password,
						onValueChange = { password = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Mật khẩu") },
						singleLine = true,
						visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
					)

					Spacer(modifier = Modifier.height(12.dp))

					OutlinedTextField(
						value = confirmPassword,
						onValueChange = { confirmPassword = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Xác nhận mật khẩu") },
						singleLine = true,
						visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
					)

					TextButton(
						onClick = { isPasswordVisible = !isPasswordVisible },
						modifier = Modifier.align(Alignment.End)
					) {
						Text(if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu")
					}

					Button(
						onClick = {
							if (password != confirmPassword) {
								context.toast("Mật khẩu xác nhận không khớp")
								return@Button
							}
							scope.launch {
								runCatching { onRegister(fullName, email, password) }
									.onSuccess {
										context.toast("Đăng ký thành công")
										onNavigateLogin()
									}
									.onFailure { context.toast(it.toRegisterErrorMessage("Đăng ký thất bại")) }
							}
						},
						modifier = Modifier.fillMaxWidth(),
						contentPadding = PaddingValues(vertical = 14.dp),
						shape = RoundedCornerShape(16.dp)
					) {
						Text("Tạo tài khoản")
					}

					Spacer(modifier = Modifier.height(12.dp))

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
}

private fun Context.toast(message: String) {
	android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
}

private fun Throwable.toRegisterErrorMessage(defaultMessage: String): String {
	if (this is retrofit2.HttpException) {
		try {
			val errorJsonString = this.response()?.errorBody()?.string()
			if (errorJsonString != null) {
				val jsonObject = org.json.JSONObject(errorJsonString)
				if (jsonObject.has("message")) {
					return jsonObject.getString("message")
				}
			}
		} catch (e: Exception) {
			// ignore and fallback
		}
	}

	val errorText = buildString {
		append(message.orEmpty())
		cause?.message?.let { append(' ').append(it) }
	}.lowercase()

	return when {
		errorText.contains("failed to connect") ||
			errorText.contains("connection refused") ||
			errorText.contains("unable to resolve host") ||
			errorText.contains("connect timed out") ||
			errorText.contains("timeout") ->
			"Không kết nối được tới backend. Hãy kiểm tra ExpressJS đang chạy ở port 3001."
		else -> message?.takeIf { it.isNotBlank() } ?: defaultMessage
	}
}
