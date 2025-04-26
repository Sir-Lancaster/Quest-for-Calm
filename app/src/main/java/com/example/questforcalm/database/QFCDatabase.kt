package com.example.questforcalm.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.questforcalm.dao.MoodLogDao
import com.example.questforcalm.models.MoodLog

@Database(
    entities = [MoodLog::class],
    version = 1
)
abstract class QFCDatabase : RoomDatabase() {
    abstract fun moodLogDao(): MoodLogDao
}