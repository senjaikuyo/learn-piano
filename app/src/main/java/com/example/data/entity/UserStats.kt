package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1, // Single-row configuration
    val dailyStreak: Int = 1,
    val lastPlayedTimestamp: Long = 0L,
    val totalPlayTimeSecs: Long = 0L,
    val level: Int = 1,
    val xp: Int = 0
)
