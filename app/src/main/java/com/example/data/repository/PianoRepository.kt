package com.example.data.repository

import com.example.data.dao.PianoDao
import com.example.data.entity.SongProgress
import com.example.data.entity.UserStats
import com.example.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PianoRepository(private val pianoDao: PianoDao) {

    val allSongs: List<Song> = Song.DEFAULT_SONGS

    val allSongProgress: Flow<List<SongProgress>> = pianoDao.getAllSongProgress()

    val userStats: Flow<UserStats?> = pianoDao.getUserStats()

    fun getSong(songId: String): Song? {
        return allSongs.find { it.id == songId }
    }

    fun getSongProgress(songId: String): Flow<SongProgress?> {
        return pianoDao.getSongProgress(songId)
    }

    suspend fun saveSongProgress(progress: SongProgress) {
        pianoDao.insertSongProgress(progress)
    }

    suspend fun toggleFavorite(songId: String, currentStatus: Boolean) {
        pianoDao.toggleFavorite(songId, !currentStatus)
    }

    suspend fun saveUserStats(stats: UserStats) {
        pianoDao.insertUserStats(stats)
    }

    suspend fun logPracticeSession(songId: String, score: Int, stars: Int, secondsPlayed: Long) {
        // Save song progress
        val existingProgress = pianoDao.getSongProgress(songId).firstOrNull()
        val newHighScore = maxOf(existingProgress?.highScore ?: 0, score)
        val newStars = maxOf(existingProgress?.stars ?: 0, stars)
        val newProgress = SongProgress(
            songId = songId,
            highScore = newHighScore,
            stars = newStars,
            isFavorite = existingProgress?.isFavorite ?: false,
            isCompleted = true
        )
        pianoDao.insertSongProgress(newProgress)

        // Save stats, update XP, levels, and playtime
        val currentStats = pianoDao.getUserStats().firstOrNull() ?: UserStats()
        val playedTimestamp = System.currentTimeMillis()
        
        // Streak verification (if played yesterday/today, maintain/increment streak)
        val msInDay = 24 * 60 * 60 * 1000L
        val lastPlayed = currentStats.lastPlayedTimestamp
        val currentStreak = when {
            lastPlayed == 0L -> 1
            playedTimestamp - lastPlayed > 2 * msInDay -> 1 // Broken streak
            playedTimestamp - lastPlayed > msInDay -> currentStats.dailyStreak + 1 // New day
            else -> currentStats.dailyStreak // Played on the same day
        }

        val totalXp = currentStats.xp + (score / 10) + 20 // 20 XP guarantee
        val newLevel = 1 + (totalXp / 100) // 100 XP per level

        val updatedStats = UserStats(
            id = 1,
            dailyStreak = currentStreak,
            lastPlayedTimestamp = playedTimestamp,
            totalPlayTimeSecs = currentStats.totalPlayTimeSecs + secondsPlayed,
            level = newLevel,
            xp = totalXp
        )
        pianoDao.insertUserStats(updatedStats)
    }
}
