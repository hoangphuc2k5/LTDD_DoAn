package com.example.tegram.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey val date: String, // format: yyyy-MM-dd
    val wordsLearnedCount: Int,
    val reviewsCount: Int,
    val correctReviewsCount: Int
)
