package com.example.data.model

import androidx.annotation.Keep

@Keep
data class Note(
    val pitch: Int,       // MIDI Note (e.g. 60 = C4/DO)
    val timeMs: Long,     // Milliseconds from start
    val durationMs: Long = 400
)
