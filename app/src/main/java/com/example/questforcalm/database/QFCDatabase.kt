package com.example.questforcalm.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.questforcalm.dao.MoodLogDao
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.MoodLog
import com.example.questforcalm.models.UserProgress

@Database(
    entities = [MoodLog::class, UserProgress::class],
    version = 2
)
abstract class QFCDatabase : RoomDatabase() {
    abstract fun moodLogDao(): MoodLogDao
    abstract fun userProgressDao(): UserProgressDao
}