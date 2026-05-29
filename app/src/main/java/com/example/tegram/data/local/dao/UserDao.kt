package com.example.tegram.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tegram.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun upsert(user: UserEntity): Long

	@Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
	fun observeById(uid: String): Flow<UserEntity?>

	@Query("SELECT * FROM users WHERE email = :email LIMIT 1")
	fun getByEmail(email: String): UserEntity?
}
