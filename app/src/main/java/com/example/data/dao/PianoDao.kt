package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.SongProgress
import com.example.data.entity.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface PianoDao {
    @Query("SELECT * FROM song_progress")
    fun getAllSongProgress(): Flow<List<SongProgress>>

    @Query("SELECT * FROM song_progress WHERE songId = :songId")
    fun getSongProgress(songId: String): Flow<SongProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongProgress(progress: SongProgress)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Query("UPDATE song_progress SET isFavorite = :isFav WHERE songId = :songId")
    suspend fun toggleFavorite(songId: String, isFav: Boolean)
}
