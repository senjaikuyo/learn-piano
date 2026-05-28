package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.PianoSynthesizer
import com.example.data.database.PianoDatabase
import com.example.data.entity.SongProgress
import com.example.data.entity.UserStats
import com.example.data.model.Difficulty
import com.example.data.model.Note
import com.example.data.model.Song
import com.example.data.repository.PianoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Home : Screen()
    object PianoUtama : Screen()
    object SongList : Screen()
    data class SongDetail(val song: Song) : Screen()
    data class PracticeMode(val song: Song) : Screen()
    object Stats : Screen()
    object Profile : Screen()
    object SettingsPiano : Screen()
}

class PianoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PianoDatabase.getDatabase(application)
    private val repository = PianoRepository(db.pianoDao())

    // Sound engine instance
    val synthesizer = PianoSynthesizer()

    // Screen Management
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation Backstack (Simple representation)
    private val screenHistory = mutableListOf<Screen>()

    // Current Active Song
    private val _selectedSong = MutableStateFlow<Song>(repository.allSongs.first())
    val selectedSong: StateFlow<Song> = _selectedSong.asStateFlow()

    // Preferences & Keyboard Settings (Section 4.4 / 4.10)
    private val _instrument = MutableStateFlow(PianoSynthesizer.InstrumentType.GRAND_PIANO)
    val instrument = _instrument.asStateFlow()

    private val _masterVolume = MutableStateFlow(1.0f)
    val masterVolume = _masterVolume.asStateFlow()

    private val _reverbEnabled = MutableStateFlow(false)
    val reverbEnabled = _reverbEnabled.asStateFlow()

    private val _showNoteNumbers = MutableStateFlow(true)
    val showNoteNumbers = _showNoteNumbers.asStateFlow()

    private val _showSolfege = MutableStateFlow(true)
    val showSolfege = _showSolfege.asStateFlow()

    private val _keySizeScale = MutableStateFlow(1.0f) // 小 0.8 / 中 1.0 / 大 1.25
    val keySizeScale = _keySizeScale.asStateFlow()

    // User Profile Info
    private val _userName = MutableStateFlow("Sobat NadaKu")
    val userName = _userName.asStateFlow()

    // Reactive lists from database
    val songProgressList: StateFlow<List<SongProgress>> = repository.allSongProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStats())

    // Combine song information directly with its Room progress
    val annotatedSongs: StateFlow<List<Pair<Song, SongProgress?>>> = combine(
        MutableStateFlow(repository.allSongs),
        songProgressList
    ) { songs, progress ->
        songs.map { song ->
            song to progress.find { it.songId == song.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- GAME ENGINE: Mode Latihan (PRD Section 4.7) ---
    private var gameLoopJob: Job? = null
    private var songPlaybackJob: Job? = null

    private val _isPracticeRunning = MutableStateFlow(false)
    val isPracticeRunning = _isPracticeRunning.asStateFlow()

    // Notes that are currently flowing down the screen
    private val _activeFallingNotes = MutableStateFlow<List<Note>>(emptyList())
    val activeFallingNotes = _activeFallingNotes.asStateFlow()

    // Highlighted keys to light up on keyboard (tutorial mode)
    private val _highlightedPitches = MutableStateFlow<Set<Int>>(emptySet())
    val highlightedPitches = _highlightedPitches.asStateFlow()

    private val _practiceScore = MutableStateFlow(0)
    val practiceScore = _practiceScore.asStateFlow()

    private val _perfectCount = MutableStateFlow(0)
    val perfectCount = _perfectCount.asStateFlow()

    private val _goodCount = MutableStateFlow(0)
    val goodCount = _goodCount.asStateFlow()

    private val _missCount = MutableStateFlow(0)
    val missCount = _missCount.asStateFlow()

    private val _practiceNoteIndex = MutableStateFlow(0)
    val practiceNoteIndex = _practiceNoteIndex.asStateFlow()

    private val _elapsedTimeMs = MutableStateFlow(0L)
    val elapsedTimeMs = _elapsedTimeMs.asStateFlow()

    init {
        // Bootstrap base userstats in DB
        viewModelScope.launch {
            repository.saveUserStats(UserStats(id = 1, dailyStreak = 1, lastPlayedTimestamp = System.currentTimeMillis() - 86400000))
        }
    }

    // --- NAVIGATION HELPERS ---
    fun navigateTo(screen: Screen) {
        screenHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
        } else {
            _currentScreen.value = Screen.Home
        }
    }

    // --- SOUND ENGINE SETTINGS TRIGGERS ---
    fun updateInstrument(type: PianoSynthesizer.InstrumentType) {
        _instrument.value = type
        synthesizer.setInstrument(type)
    }

    fun updateVolume(vol: Float) {
        _masterVolume.value = vol
        synthesizer.setVolume(vol)
    }

    fun updateReverb(enabled: Boolean) {
        _reverbEnabled.value = enabled
        synthesizer.setReverbEnabled(enabled)
    }

    fun toggleNoteNumbers() {
        _showNoteNumbers.value = !_showNoteNumbers.value
    }

    fun toggleSolfege() {
        _showSolfege.value = !_showSolfege.value
    }

    fun updateKeyScale(scale: Float) {
        _keySizeScale.value = scale
    }

    fun updateUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name
        }
    }

    fun setSong(song: Song) {
        _selectedSong.value = song
    }

    fun toggleSongFavorite(songId: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(songId, currentFav)
        }
    }

    fun triggerKeyManual(pitch: Int) {
        synthesizer.playNote(pitch)
        
        // In Practice Mode, let's check matches
        if (_isPracticeRunning.value) {
            checkUserPressMatch(pitch)
        }
    }

    // --- PRACTICE GAME LOOP IMPLEMENTATION ---
    fun startPractice(song: Song) {
        stopPractice() // Safety resets
        _isPracticeRunning.value = true
        _practiceScore.value = 0
        _perfectCount.value = 0
        _goodCount.value = 0
        _missCount.value = 0
        _practiceNoteIndex.value = 0
        _elapsedTimeMs.value = 0L
        _activeFallingNotes.value = emptyList()
        _highlightedPitches.value = emptySet()

        val startTime = System.currentTimeMillis()
        val totalDurationMs = song.notes.maxByOrNull { it.timeMs + it.durationMs }?.let { it.timeMs + it.durationMs } ?: 5000L

        // Job 1: Real-time falling notes display scroll
        gameLoopJob = viewModelScope.launch {
            while (_isPracticeRunning.value) {
                val currentElapsed = System.currentTimeMillis() - startTime
                _elapsedTimeMs.value = currentElapsed

                // Filter notes that will appear in the next 3 seconds (falling interval)
                val visibleWindow = 3000L
                val falling = song.notes.filter { 
                    it.timeMs in currentElapsed..(currentElapsed + visibleWindow)
                }
                _activeFallingNotes.value = falling

                // Highlight notes that are about to hit the bar (within 400ms target line)
                val activeTarget = song.notes.filter {
                    Math.abs(it.timeMs - currentElapsed) < 400L
                }.map { it.pitch }.toSet()
                _highlightedPitches.value = activeTarget

                // Automatically miss notes that have passed past 500ms without being clicked
                val missedNoteCount = song.notes.count {
                    currentElapsed - it.timeMs > 500L && it.timeMs >= 0L && song.notes.indexOf(it) >= _practiceNoteIndex.value
                }
                if (missedNoteCount > _missCount.value) {
                    val difference = missedNoteCount - _missCount.value
                    _missCount.value = missedNoteCount
                    _practiceNoteIndex.value = _practiceNoteIndex.value + difference
                }

                if (currentElapsed > totalDurationMs + 1000L) {
                    endPractice(song)
                    break
                }
                delay(30) // Smooth refresh ~33fps
            }
        }

        // Job 2: Background playback assistance to make it beautiful (optional but lets notes play or sound)
        songPlaybackJob = viewModelScope.launch {
            // Optional tutorial playback helper
        }
    }

    fun stopPractice() {
        _isPracticeRunning.value = false
        gameLoopJob?.cancel()
        songPlaybackJob?.cancel()
        _activeFallingNotes.value = emptyList()
        _highlightedPitches.value = emptySet()
    }

    private fun checkUserPressMatch(pressedPitch: Int) {
        val currentElapsed = _elapsedTimeMs.value
        val targetNotes = _activeFallingNotes.value.filter { it.pitch == pressedPitch }

        if (targetNotes.isNotEmpty()) {
            // Find closest note
            val closest = targetNotes.minByOrNull { Math.abs(it.timeMs - currentElapsed) } ?: return
            val delta = Math.abs(closest.timeMs - currentElapsed)

            if (delta < 200L) {
                // Perfect hit!
                _perfectCount.value++
                _practiceScore.value += 100
                _practiceNoteIndex.value++
                // Remove from local trace to avoid double hits
                removeActiveNote(closest)
            } else if (delta < 450L) {
                // Good hit!
                _goodCount.value++
                _practiceScore.value += 50
                _practiceNoteIndex.value++
                removeActiveNote(closest)
            }
        }
    }

    private fun removeActiveNote(note: Note) {
        _activeFallingNotes.value = _activeFallingNotes.value.filter { it != note }
    }

    private fun endPractice(song: Song) {
        val score = _practiceScore.value
        val perfects = _perfectCount.value
        val goods = _goodCount.value
        val totalHits = perfects + goods
        val accuracy = if (song.notes.isNotEmpty()) (totalHits.toFloat() / song.notes.size) * 100 else 0f
        
        val stars = when {
            accuracy >= 90f -> 3
            accuracy >= 65f -> 2
            accuracy >= 40f -> 1
            else -> 0
        }

        viewModelScope.launch {
            repository.logPracticeSession(
                songId = song.id,
                score = score,
                stars = stars,
                secondsPlayed = (song.notes.maxOfOrNull { it.timeMs } ?: 1000L) / 1000
            )
        }
        _isPracticeRunning.value = false
    }
}
