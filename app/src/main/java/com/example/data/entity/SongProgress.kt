package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_progress")
data class SongProgress(
    @PrimaryKey val songId: String,
    val highScore: Int = 0,
    val stars: Int = 0,
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false
)
