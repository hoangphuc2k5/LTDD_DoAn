package com.example.tegram.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tegram.data.local.entity.DailyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProgressDao {
    @Query("SELECT * FROM daily_progress ORDER BY date DESC LIMIT :limit")
    fun observeRecentProgress(limit: Int = 7): Flow<List<DailyProgressEntity>>

    @Query("SELECT * FROM daily_progress WHERE date = :date LIMIT 1")
    suspend fun getProgressForDate(date: String): DailyProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: DailyProgressEntity)

    @Query("DELETE FROM daily_progress")
    suspend fun clearAll(): Int
}
