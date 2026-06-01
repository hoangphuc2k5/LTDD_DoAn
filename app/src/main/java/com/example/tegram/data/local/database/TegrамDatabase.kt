package com.example.tegram.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tegram.data.local.dao.UserDao
import com.example.tegram.data.local.dao.DailyProgressDao
import com.example.tegram.data.local.entity.UserEntity
import com.example.tegram.data.local.entity.DailyProgressEntity

@Database(
	entities = [UserEntity::class, DailyProgressEntity::class],
	version = 3,
	exportSchema = false
)
abstract class TegramDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun dailyProgressDao(): DailyProgressDao
}
