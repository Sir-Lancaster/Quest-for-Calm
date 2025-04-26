package com.example.questforcalm.dao
import androidx.room.Dao
import androidx.room.Insert
import com.example.questforcalm.models.MoodLog

@Dao
interface MoodLogDao {
    @Insert
    suspend fun insertMoodLog(moodLog: MoodLog)
}