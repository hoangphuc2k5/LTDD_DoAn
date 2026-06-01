package com.example.tegram.data.repository

import com.example.tegram.data.local.dao.UserDao
import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import com.example.tegram.data.mapper.toDomain
import com.example.tegram.data.mapper.toEntity
import com.example.tegram.data.mapper.toSyncRequest
import com.example.tegram.data.remote.api.UserApiService
import com.example.tegram.data.remote.dto.request.LoginRequest
import com.example.tegram.data.remote.dto.request.RegisterRequest
import com.example.tegram.data.remote.dto.request.UpdateUserRequest
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
	private val firebaseAuth: FirebaseAuth?,
	private val userDao: UserDao,
	private val userPreferencesDataStore: UserPreferencesDataStore,
	private val userApiService: UserApiService
) : UserRepository {
	@OptIn(ExperimentalCoroutinesApi::class)
	override val currentUser: Flow<UserProfile?> = userPreferencesDataStore.currentUserIdFlow.flatMapLatest { uid ->
		if (uid.isNullOrBlank()) {
			flowOf(null)
		} else {
			userDao.observeById(uid).map { entity -> entity?.toDomain() }
		}
	}.distinctUntilChanged()

	override suspend fun loginWithEmail(email: String, password: String): UserProfile {
		val normalizedEmail = email.trim().lowercase()
		val response = userApiService.login(
			LoginRequest(
				email = normalizedEmail,
				password = password.trim()
			)
		)
		if (!response.success || response.user == null) {
			error(response.message ?: "Đăng nhập thất bại từ backend")
		}

		return persistSession(response.user.toDomain(), response.token)
	}

	override suspend fun registerWithEmail(fullName: String, email: String, password: String): UserProfile {
		val normalizedEmail = email.trim().lowercase()
		val response = userApiService.register(
			RegisterRequest(
				fullName = fullName.trim(),
				email = normalizedEmail,
				password = password.trim()
			)
		)
		if (!response.success || response.user == null) {
			error(response.message ?: "Đăng ký thất bại từ backend")
		}

		return persistSession(response.user.toDomain(), response.token)
	}

	override suspend fun loginWithGoogle(fullName: String?, email: String, photoUrl: String?): UserProfile {
		val normalizedEmail = email.trim().lowercase()
		val profile = UserProfile(
			uid = normalizedEmail,
			fullName = fullName?.takeIf { it.isNotBlank() } ?: normalizedEmail.substringBefore("@"),
			email = normalizedEmail,
			provider = "google",
			photoUrl = photoUrl,
			isGoogleUser = true,
			syncedAt = System.currentTimeMillis()
		)

		val response = userApiService.syncUser(profile.toSyncRequest())
		if (!response.success || response.user == null) {
			error(response.message ?: "Đăng nhập Google thất bại từ backend")
		}

		return persistSession(response.user.toDomain(), response.token)
	}

	override suspend fun updateProfile(fullName: String?, photoUrl: String?): UserProfile {
		val uid = userPreferencesDataStore.currentUserIdFlow.first()
			?: error("User not logged in")

		val response = userApiService.updateProfile(
			uid = uid,
			request = UpdateUserRequest(fullName = fullName, photoUrl = photoUrl)
		)

		if (!response.success || response.user == null) {
			error(response.message ?: "Cập nhật hồ sơ thất bại")
		}

		return persistSession(response.user.toDomain())
	}

	override suspend fun logout() {
		firebaseAuth?.signOut()
		userPreferencesDataStore.clearCurrentUser()
	}

	override suspend fun updateUserProgress(
		streak: Int,
		level: String,
		wordsLearned: Int,
		totalReviews: Int,
		correctReviews: Int
	): UserProfile {
		val uid = userPreferencesDataStore.currentUserIdFlow.first() ?: error("Người dùng chưa đăng nhập")
		val existing = userDao.observeById(uid).first() ?: error("Không tìm thấy người dùng trong CSDL")

		val updatedProfile = existing.toDomain().copy(
			streak = streak,
			level = level,
			wordsLearned = wordsLearned,
			totalReviews = totalReviews,
			correctReviews = correctReviews,
			syncedAt = System.currentTimeMillis()
		)

		// 1. Lưu local
		persistSession(updatedProfile)

		// 2. Đồng bộ server (không để lỗi network chặn trải nghiệm người dùng)
		runCatching {
			val response = userApiService.syncUser(updatedProfile.toSyncRequest())
			if (response.success && response.user != null) {
				persistSession(response.user.toDomain())
			}
		}

		return updatedProfile
	}

	private suspend fun persistSession(profile: UserProfile): UserProfile {
	private suspend fun persistSession(profile: UserProfile, token: String? = null): UserProfile {
		withContext(Dispatchers.IO) {
			userDao.upsert(profile.toEntity())
			userPreferencesDataStore.saveCurrentUser(profile)
			token?.let { userPreferencesDataStore.saveAuthToken(it) }
		}
		return profile
	}
}
