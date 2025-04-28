package com.example.questforcalm.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.questforcalm.dao.MoodLogDao
import com.example.questforcalm.dao.QuestDao
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.MoodLog
import com.example.questforcalm.models.UserProgress
import com.example.questforcalm.models.Quest

@Database(
    entities = [MoodLog::class, UserProgress::class, Quest::class],
    version = 3
)
abstract class QFCDatabase : RoomDatabase() {
    abstract fun moodLogDao(): MoodLogDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun questDao(): QuestDao
}