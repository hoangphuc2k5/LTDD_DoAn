package com.example.tegram.data.repository

import com.example.tegram.data.local.dao.UserDao
import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import com.example.tegram.data.mapper.toDomain
import com.example.tegram.data.mapper.toEntity
import com.example.tegram.data.mapper.toSyncRequest
import com.example.tegram.data.remote.api.UserApiService
import com.example.tegram.data.remote.dto.request.LoginRequest
import com.example.tegram.data.remote.dto.request.RegisterRequest
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserRepositoryImpl(
	private val firebaseAuth: FirebaseAuth?,
	private val userDao: UserDao,
	private val userPreferencesDataStore: UserPreferencesDataStore,
	private val userApiService: UserApiService
) : UserRepository {
	private val secureRandom = SecureRandom()

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
		val backendProfile = runCatching {
			userApiService.login(
				LoginRequest(
					email = normalizedEmail,
					password = password.trim()
				)
			).user?.toDomain()
		}.getOrNull()
		if (backendProfile != null) {
			val localExisting = withContext(Dispatchers.IO) { userDao.getByEmail(normalizedEmail) }
			return persistSession(
				backendProfile.copy(
					passwordHash = localExisting?.passwordHash,
					passwordSalt = localExisting?.passwordSalt
				)
			)
		}

		val localUser = withContext(Dispatchers.IO) { userDao.getByEmail(normalizedEmail) }
		if (localUser != null) {
			val passwordHash = localUser.passwordHash
			val passwordSalt = localUser.passwordSalt
			if (!passwordHash.isNullOrBlank() && !passwordSalt.isNullOrBlank() &&
				passwordHash == hashPassword(password.trim(), passwordSalt)
			) {
				return persistSession(localUser.toDomain())
			}
		}

		return runCatching {
			val auth = requireFirebaseAuth()
			val result = auth.signInWithEmailAndPassword(normalizedEmail, password.trim()).await()
			val firebaseUser = result.user ?: auth.currentUser ?: error("Không thể đọc thông tin người dùng sau khi đăng nhập")
			val passwordSalt = generateSalt()
			persistUser(
				uid = firebaseUser.uid,
				displayName = firebaseUser.displayName,
				email = firebaseUser.email,
				photoUrl = firebaseUser.photoUrl?.toString(),
				provider = "email",
				isGoogleUser = false,
				passwordHash = hashPassword(password.trim(), passwordSalt),
				passwordSalt = passwordSalt
			)
		}.getOrElse {
			localUser?.let {
				if (it.passwordHash.isNullOrBlank() || it.passwordSalt.isNullOrBlank()) {
					error("Tài khoản local chưa có mật khẩu để đăng nhập")
				}
				if (it.passwordHash != hashPassword(password.trim(), it.passwordSalt)) {
					error("Sai email hoặc mật khẩu")
				}
				return persistSession(it.toDomain())
			}
			throw it
		}
	}

	override suspend fun registerWithEmail(fullName: String, email: String, password: String): UserProfile {
		val normalizedEmail = email.trim().lowercase()
		val passwordSalt = generateSalt()
		val passwordHash = hashPassword(password.trim(), passwordSalt)
		val backendProfile = runCatching {
			userApiService.register(
				RegisterRequest(
					fullName = fullName.trim(),
					email = normalizedEmail,
					password = password.trim()
				)
			).user?.toDomain()
		}.getOrNull()
		if (backendProfile != null) {
			return persistSession(
				backendProfile.copy(
					passwordHash = passwordHash,
					passwordSalt = passwordSalt
				)
			)
		}

		val localExisting = withContext(Dispatchers.IO) { userDao.getByEmail(normalizedEmail) }
		if (localExisting != null) {
			error("Email đã được sử dụng")
		}

		return runCatching {
			val auth = requireFirebaseAuth()
			val result = auth.createUserWithEmailAndPassword(normalizedEmail, password.trim()).await()
			val firebaseUser = result.user ?: auth.currentUser ?: error("Không thể tạo tài khoản")

			if (fullName.isNotBlank()) {
				firebaseUser.updateProfile(
					UserProfileChangeRequest.Builder()
						.setDisplayName(fullName.trim())
						.build()
				).await()
			}

			persistUser(
				uid = firebaseUser.uid,
				displayName = fullName,
				email = firebaseUser.email,
				photoUrl = firebaseUser.photoUrl?.toString(),
				provider = "email",
				isGoogleUser = false,
				passwordHash = passwordHash,
				passwordSalt = passwordSalt
			)
		}.getOrElse {
			val localProfile = UserProfile(
				uid = normalizedEmail,
				fullName = fullName.trim().ifBlank { normalizedEmail.substringBefore("@") },
				email = normalizedEmail,
				provider = "local",
				photoUrl = null,
				isGoogleUser = false,
				passwordHash = passwordHash,
				passwordSalt = passwordSalt,
				syncedAt = System.currentTimeMillis()
			)
			persistSession(localProfile)
		}
	}

	override suspend fun loginWithGoogle(fullName: String?, email: String, photoUrl: String?): UserProfile {
		val normalizedEmail = email.trim().lowercase()
		val existing = withContext(Dispatchers.IO) { userDao.getByEmail(normalizedEmail) }
		val profile = UserProfile(
			uid = existing?.uid ?: normalizedEmail,
			fullName = fullName?.takeIf { it.isNotBlank() }
				?: existing?.fullName
				?: normalizedEmail.substringBefore("@"),
			email = normalizedEmail,
			provider = "google",
			photoUrl = photoUrl,
			isGoogleUser = true,
			passwordHash = existing?.passwordHash,
			passwordSalt = existing?.passwordSalt,
			syncedAt = System.currentTimeMillis()
		)
		return persistSession(profile)
	}

	override suspend fun logout() {
		firebaseAuth?.signOut()
		userPreferencesDataStore.clearCurrentUser()
	}

	private fun requireFirebaseAuth(): FirebaseAuth =
		firebaseAuth ?: error("Firebase Auth chưa được cấu hình. Hãy thêm google-services.json để bật Email/Google login.")

	private suspend fun persistUser(
		uid: String,
		displayName: String?,
		email: String?,
		photoUrl: String?,
		provider: String,
		isGoogleUser: Boolean
	): UserProfile = persistUser(uid, displayName, email, photoUrl, provider, isGoogleUser, null, null)

	private suspend fun persistUser(
		uid: String,
		displayName: String?,
		email: String?,
		photoUrl: String?,
		provider: String,
		isGoogleUser: Boolean,
		passwordHash: String?,
		passwordSalt: String?
	): UserProfile {
		val profile = UserProfile(
			uid = uid,
			fullName = displayName?.takeIf { it.isNotBlank() } ?: email.orEmpty().substringBefore("@"),
			email = email.orEmpty(),
			provider = provider,
			photoUrl = photoUrl,
			isGoogleUser = isGoogleUser,
			passwordHash = passwordHash,
			passwordSalt = passwordSalt,
			syncedAt = System.currentTimeMillis()
		)

		withContext(Dispatchers.IO) {
			userDao.upsert(profile.toEntity())
			userPreferencesDataStore.saveCurrentUser(profile)
			runCatching {
				userApiService.syncUser(profile.toSyncRequest())
			}
		}

		return profile
	}

	private suspend fun persistSession(profile: UserProfile): UserProfile {
		withContext(Dispatchers.IO) {
			userDao.upsert(profile.toEntity())
			userPreferencesDataStore.saveCurrentUser(profile)
			runCatching { userApiService.syncUser(profile.toSyncRequest()) }
		}
		return profile
	}

	private fun generateSalt(): String {
		val bytes = ByteArray(16)
		secureRandom.nextBytes(bytes)
		return Base64.encodeToString(bytes, Base64.NO_WRAP)
	}

	private fun hashPassword(password: String, salt: String): String {
		val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 10_000, 256)
		val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
		val encoded = factory.generateSecret(spec).encoded
		return Base64.encodeToString(encoded, Base64.NO_WRAP)
	}
}
