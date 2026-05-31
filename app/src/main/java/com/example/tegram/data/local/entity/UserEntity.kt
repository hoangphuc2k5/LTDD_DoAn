package com.example.tegram.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
	@PrimaryKey val uid: String,
	val fullName: String,
	val email: String,
	val provider: String,
	val photoUrl: String?,
	val isGoogleUser: Boolean,
	val passwordHash: String?,
	val passwordSalt: String?,
	val streak: Int,
	val level: String,
	val wordsLearned: Int,
	val totalReviews: Int,
	val correctReviews: Int,
	val syncedAt: Long
)
