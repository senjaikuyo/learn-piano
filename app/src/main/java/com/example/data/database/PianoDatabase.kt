package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.PianoDao
import com.example.data.entity.SongProgress
import com.example.data.entity.UserStats

@Database(
    entities = [SongProgress::class, UserStats::class],
    version = 1,
    exportSchema = false
)
abstract class PianoDatabase : RoomDatabase() {
    abstract fun pianoDao(): PianoDao

    companion object {
        @Volatile
        private var INSTANCE: PianoDatabase? = null

        fun getDatabase(context: Context): PianoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PianoDatabase::class.java,
                    "nadaku_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
