package com.example.tegram.data.mapper

import com.example.tegram.data.local.entity.UserEntity
import com.example.tegram.data.remote.dto.request.UserSyncRequest
import com.example.tegram.data.remote.dto.response.RemoteUserDto
import com.example.tegram.domain.model.UserProfile

fun UserEntity.toDomain(): UserProfile = UserProfile(
	uid = uid,
	fullName = fullName,
	email = email,
	provider = provider,
	photoUrl = photoUrl,
	isGoogleUser = isGoogleUser,
	passwordHash = passwordHash,
	passwordSalt = passwordSalt,
	streak = streak,
	level = level,
	wordsLearned = wordsLearned,
	totalReviews = totalReviews,
	correctReviews = correctReviews,
	syncedAt = syncedAt
)

fun UserProfile.toEntity(): UserEntity = UserEntity(
	uid = uid,
	fullName = fullName,
	email = email,
	provider = provider,
	photoUrl = photoUrl,
	isGoogleUser = isGoogleUser,
	passwordHash = passwordHash,
	passwordSalt = passwordSalt,
	streak = streak,
	level = level,
	wordsLearned = wordsLearned,
	totalReviews = totalReviews,
	correctReviews = correctReviews,
	syncedAt = syncedAt
)

fun UserProfile.toSyncRequest(): UserSyncRequest = UserSyncRequest(
	uid = uid,
	fullName = fullName,
	email = email,
	provider = provider,
	photoUrl = photoUrl,
	isGoogleUser = isGoogleUser,
	streak = streak,
	level = level,
	wordsLearned = wordsLearned,
	totalReviews = totalReviews,
	correctReviews = correctReviews,
	syncedAt = syncedAt
)

fun RemoteUserDto.toDomain(): UserProfile = UserProfile(
	uid = uid,
	fullName = fullName,
	email = email,
	provider = provider,
	photoUrl = photoUrl,
	isGoogleUser = isGoogleUser,
	streak = streak,
	level = level,
	wordsLearned = wordsLearned,
	totalReviews = totalReviews,
	correctReviews = correctReviews,
	syncedAt = syncedAt
)
