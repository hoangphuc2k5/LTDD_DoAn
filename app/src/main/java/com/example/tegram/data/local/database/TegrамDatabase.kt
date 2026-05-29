package com.example.tegram.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tegram.data.local.dao.UserDao
import com.example.tegram.data.local.entity.UserEntity

@Database(
	entities = [UserEntity::class],
	version = 2,
	exportSchema = false
)
abstract class TegramDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
}
